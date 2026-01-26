package com.eatssu.android.di.network


import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * TokenInterceptor : API 요청 시 AccessToken을 헤더에 추가하는 인터셉터
 * */
class TokenInterceptor @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
) : Interceptor {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { getAccessTokenUseCase() }
        val originalRequest = chain.request()

        val requestBuilder = originalRequest.newBuilder()
            .header(HEADER_CONTENT_TYPE, "application/json")

        if (accessToken.isNotBlank()) {
            requestBuilder.header(HEADER_AUTHORIZATION, "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}
