package com.eatssu.android.util


import com.eatssu.android.App
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val userToken = App.token_prefs.accessToken

        if (userToken.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val tokenAddedRequest = originalRequest.newBuilder().header(
            "AUTHORIZATION", userToken
        ).build()
//
        return chain.proceed(tokenAddedRequest)
    }
}