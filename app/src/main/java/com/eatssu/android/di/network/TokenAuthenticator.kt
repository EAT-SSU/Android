package com.eatssu.android.di.network

import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.ReissueAndStoreResult
import com.eatssu.android.domain.usecase.auth.ReissueAndStoreTokenUseCase
import com.eatssu.android.presentation.base.LogoutReason
import com.eatssu.android.presentation.base.TokenEventBus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val reissueAndStoreTokenUseCase: ReissueAndStoreTokenUseCase,
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

                Timber.d("TokenAuthenticator → attempting token reissue")
                when (val result = reissueAndStoreTokenUseCase()) {
                    is ReissueAndStoreResult.Success -> response.request.newBuilder()
                        .header("Authorization", "Bearer ${result.accessToken}")
                        .build()

                    is ReissueAndStoreResult.MissingRefreshToken -> {
                        Timber.e("TokenAuthenticator → refreshToken is blank; forcing logout")
                        logoutUseCase()
                        TokenEventBus.notifyTokenExpired(LogoutReason.MISSING_REFRESH_TOKEN)
                        null
                    }

                    is ReissueAndStoreResult.RefreshInvalid -> {
                        Timber.e(
                            "TokenAuthenticator → refresh invalid: code=${result.responseCode}, message=${result.message}"
                        )
                        logoutUseCase()
                        TokenEventBus.notifyTokenExpired(LogoutReason.REFRESH_TOKEN_EXPIRED)
                        null
                    }

                    is ReissueAndStoreResult.TransientFailure -> {
                        Timber.w(
                            result.throwable,
                            "TokenAuthenticator → transient reissue failure: code=${result.responseCode}, message=${result.message}"
                        )
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
