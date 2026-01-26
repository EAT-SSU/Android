package com.eatssu.android.di.network

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.presentation.base.TokenEventBus
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.ReissueTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

/**
 * AccessToken이 만료되어 서버가 401 응답을 줄 때
 * '자동'으로 RefreshToken을 사용해 새 AccessToken을 발급받고,
 * 원래 요청을 새 토큰으로 다시 보내주는 클래스  - 백그라운드 스레드에서 실행
 * */
class TokenAuthenticator @Inject constructor(
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val reissueTokenUseCase: ReissueTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
) : Authenticator {

    private companion object {
        val mutex = Mutex()
    }

    /**
     * 401 Unauthorized 응답을 받았을 때 호출되는 메서드
     * @param route : 요청한 경로
     * @param response : 응답 객체
     * @return : 새로운 요청 객체
     */

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            Timber.w("401 응답 재시도 2회 초과 → 요청 중단")
            return null
        }

        return runBlocking {
            mutex.withLock {
                val currentAccessToken = getAccessTokenUseCase()
                val requestAuthHeader = response.request.header("Authorization")

                // 이미 다른 요청이 토큰을 재발급/저장한 경우, 저장된 토큰으로만 재시도
                if (!requestAuthHeader.isNullOrBlank() && requestAuthHeader != "Bearer $currentAccessToken") {
                    Timber.d("TokenAuthenticator → token already refreshed by another call; retrying with stored token")
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                }

                val refreshToken = getRefreshTokenUseCase()
                if (refreshToken.isBlank()) {
                    Timber.e("TokenAuthenticator → refreshToken is blank; forcing logout")
                    logoutUseCase()
                    TokenEventBus.notifyTokenExpired()
                    return@withLock null
                }

                Timber.d("TokenAuthenticator → attempting token reissue with refreshToken")
                when (val result = reissueTokenUseCase(refreshToken)) {
                    is ApiResult.Success -> {
                        val newAccessToken = result.data.accessToken
                        val newRefreshToken = result.data.refreshToken

                        if (newAccessToken.isBlank() || newRefreshToken.isBlank()) {
                            Timber.e("TokenAuthenticator → reissue returned blank tokens")
                            logoutUseCase()
                            TokenEventBus.notifyTokenExpired()
                            return@withLock null
                        }

                        setAccessTokenUseCase(newAccessToken)
                        setRefreshTokenUseCase(newRefreshToken)

                        Timber.d("TokenAuthenticator → token reissue success; retrying original request")
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    }

                    is ApiResult.Failure -> {
                        Timber.e(
                            "TokenAuthenticator → reissue failed: code=${result.responseCode}, message=${result.message}"
                        )

                        // Refresh token invalid/expired: force logout
                        if (result.responseCode == 401 || result.responseCode == 403) {
                            logoutUseCase()
                            TokenEventBus.notifyTokenExpired()
                        }

                        // Transient failure: don't clear local tokens; return null to propagate 401
                        null
                    }

                    is ApiResult.NetworkError -> {
                        Timber.w(result.exception, "TokenAuthenticator → reissue network error; keeping tokens")
                        null
                    }

                    is ApiResult.UnknownError -> {
                        Timber.e(result.exception, "TokenAuthenticator → reissue unknown error; keeping tokens")
                        null
                    }
                }
            }
        }
    }

    // 무한 루프 방지를 위한 재귀 호출 횟수 체크
    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse // 이전 응답
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }

        Timber.d("TokenAuthenticator → responseCount: $count")
        return count
    }
}
