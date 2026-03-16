package com.eatssu.android.di.network

import app.cash.turbine.test
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.ReissueAndStoreResult
import com.eatssu.android.domain.usecase.auth.ReissueAndStoreTokenUseCase
import com.eatssu.android.presentation.base.LogoutReason
import com.eatssu.android.presentation.base.TokenEventBus
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class TokenAuthenticatorBehaviorSpec : AppBehaviorSpec({

    given("TokenAuthenticator") {
        val getAccessTokenUseCase = mockk<GetAccessTokenUseCase>()
        val reissueAndStoreTokenUseCase = mockk<ReissueAndStoreTokenUseCase>()
        val logoutUseCase = mockk<LogoutUseCase>()
        val authenticator = TokenAuthenticator(
            getAccessTokenUseCase = getAccessTokenUseCase,
            reissueAndStoreTokenUseCase = reissueAndStoreTokenUseCase,
            logoutUseCase = logoutUseCase,
        )

        fun buildResponse(
            authHeader: String?,
            prior: Response? = null,
            withBody: Boolean = true,
        ): Response {
            val request = Request.Builder()
                .url("https://example.com")
                .apply {
                    if (authHeader != null) header("Authorization", authHeader)
                }
                .build()

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(401)
                .message("Unauthorized")
                .apply {
                    if (withBody) body("{}".toResponseBody())
                }
                .apply {
                    if (prior != null) priorResponse(prior)
                }
                .build()
        }

        coEvery { logoutUseCase() } returns Unit

        `when`("이미 2회 이상 재시도한 응답이면") {
            val prior = buildResponse("Bearer old", withBody = false)
            val response = buildResponse("Bearer old", prior = prior)

            then("재시도하지 않고 null을 반환한다") {
                authenticator.authenticate(null, response) shouldBe null
            }
        }

        `when`("다른 요청이 이미 토큰을 갱신한 경우") {
            every { getAccessTokenUseCase() } returns "new-token"
            val response = buildResponse("Bearer old-token")

            then("저장된 토큰으로 요청 헤더를 교체해 반환한다") {
                val retried = authenticator.authenticate(null, response)

                retried?.header("Authorization") shouldBe "Bearer new-token"
                coVerify(exactly = 0) { reissueAndStoreTokenUseCase() }
            }
        }

        `when`("재발급이 성공하면") {
            every { getAccessTokenUseCase() } returns "current-token"
            coEvery { reissueAndStoreTokenUseCase() } returns ReissueAndStoreResult.Success("fresh-token")
            val response = buildResponse("Bearer current-token")

            then("새 토큰으로 요청을 재구성한다") {
                val retried = authenticator.authenticate(null, response)
                retried?.header("Authorization") shouldBe "Bearer fresh-token"
            }
        }

        `when`("refresh token이 없어 재발급할 수 없으면") {
            every { getAccessTokenUseCase() } returns "current-token"
            coEvery { reissueAndStoreTokenUseCase() } returns ReissueAndStoreResult.MissingRefreshToken
            val response = buildResponse("Bearer current-token")

            then("로그아웃 후 MISSING_REFRESH_TOKEN 이벤트를 발행하고 null을 반환한다") {
                TokenEventBus.tokenExpired.test {
                    authenticator.authenticate(null, response) shouldBe null
                    awaitItem() shouldBe LogoutReason.MISSING_REFRESH_TOKEN
                    coVerify { logoutUseCase() }
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        `when`("refresh token이 만료된 경우") {
            every { getAccessTokenUseCase() } returns "current-token"
            coEvery {
                reissueAndStoreTokenUseCase()
            } returns ReissueAndStoreResult.RefreshInvalid(401, "expired")
            val response = buildResponse("Bearer current-token")

            then("로그아웃 후 REFRESH_TOKEN_EXPIRED 이벤트를 발행하고 null을 반환한다") {
                TokenEventBus.tokenExpired.test {
                    authenticator.authenticate(null, response) shouldBe null
                    awaitItem() shouldBe LogoutReason.REFRESH_TOKEN_EXPIRED
                    coVerify { logoutUseCase() }
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }

        `when`("재발급이 일시적 실패면") {
            every { getAccessTokenUseCase() } returns "current-token"
            coEvery {
                reissueAndStoreTokenUseCase()
            } returns ReissueAndStoreResult.TransientFailure(message = "timeout")
            val response = buildResponse("Bearer current-token")

            then("로그아웃 없이 null을 반환한다") {
                authenticator.authenticate(null, response) shouldBe null
                coVerify(exactly = 0) { logoutUseCase() }
            }
        }
    }
})
