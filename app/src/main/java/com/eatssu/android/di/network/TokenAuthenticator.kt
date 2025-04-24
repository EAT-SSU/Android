package com.eatssu.android.di.network

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.TokenResponse
import com.eatssu.android.domain.usecase.auth.*
import com.eatssu.android.data.service.OauthService
import com.eatssu.android.presentation.base.TokenViewModel
import kotlinx.coroutines.flow.firstOrNull
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
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val reissueTokenUseCase: ReissueTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenViewModel: TokenViewModel
) : Authenticator {

    /**
     * 401 Unauthorized 응답을h 받았을 때 호출되는 메서드
     * @param route : 요청한 경로
     * @param response : 응답 객체
     * @return : 새로운 요청 객체
     */

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            Timber.w("401 응답 재시도 2회 초과 → 요청 중단")
            return null
        }

        val expiredRefreshToken = runBlocking { getRefreshTokenUseCase() }

        return runBlocking {
            try {
                Timber.d("TokenAuthenticator → refreshToken으로 재발급 시도")

                val newTokenResponse:  BaseResponse<TokenResponse>? = reissueTokenUseCase(expiredRefreshToken).firstOrNull()
                val newAccessToken = newTokenResponse?.result?.accessToken
                val newRefreshToken = newTokenResponse?.result?.refreshToken

                if (newAccessToken != null && newRefreshToken != null) {
                    Timber.d("TokenAuthenticator → 새 토큰 발급 성공")
                    setAccessTokenUseCase(newAccessToken)
                    setRefreshTokenUseCase(newRefreshToken)
                } else {
                    // 잘못된 토큰을 받은 경우
                    Timber.e("TokenAuthenticator → 새 토큰 발급 실패")
                    logoutUseCase() // 로그아웃 처리
                    tokenViewModel.notifyTokenExpired()
                    // TODO : 로그아웃 처리 후 로그인 화면으로 이동 및 토스트
                    // 시스템 오류로 다시 로그인해주세요.


                }

                Timber.d("TokenAuthenticator → 새 토큰 저장 및 기존 API 재요청")

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newAccessToken}")
                    .build()

            } catch (e: Exception) {
                // refreshToken이 만료된 경우
                Timber.e(e, "토큰 재발급 중 예외 발생")
                logoutUseCase()
                tokenViewModel.notifyTokenExpired()
                // TODO : 로그아웃 처리 후 로그인 화면으로 이동 및 토스트
                // 로그인 시간이 만료되어 다시 로그인해 주세요.

                null
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
