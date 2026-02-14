package com.eatssu.android.di.network

import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class TokenInterceptorBehaviorSpec : AppBehaviorSpec({

    given("TokenInterceptor") {
        val getAccessTokenUseCase = mockk<GetAccessTokenUseCase>()
        val interceptor = TokenInterceptor(getAccessTokenUseCase)

        fun buildResponse(request: Request): Response =
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("{}".toResponseBody())
                .build()

        `when`("access token이 존재하면") {
            every { getAccessTokenUseCase() } returns "access-token"
            val original = Request.Builder().url("https://example.com").build()
            val chain = mockk<Interceptor.Chain>()
            val captured = slot<Request>()
            every { chain.request() } returns original
            every { chain.proceed(capture(captured)) } answers { buildResponse(captured.captured) }

            then("Content-Type과 Authorization 헤더를 추가한다") {
                interceptor.intercept(chain)

                captured.captured.header("Content-Type") shouldBe "application/json"
                captured.captured.header("Authorization") shouldBe "Bearer access-token"
            }
        }

        `when`("access token이 비어있으면") {
            every { getAccessTokenUseCase() } returns " "
            val original = Request.Builder().url("https://example.com").build()
            val chain = mockk<Interceptor.Chain>()
            val captured = slot<Request>()
            every { chain.request() } returns original
            every { chain.proceed(capture(captured)) } answers { buildResponse(captured.captured) }

            then("Content-Type만 추가하고 Authorization은 생략한다") {
                interceptor.intercept(chain)

                captured.captured.header("Content-Type") shouldBe "application/json"
                captured.captured.header("Authorization") shouldBe null
            }
        }
    }
})
