package com.eatssu.android.di.network


import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.eatssu.android.BuildConfig.BASE_URL
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.TokenResponse
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import com.eatssu.android.presentation.login.LoginActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber
import java.lang.reflect.Type
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
        private const val HEADER_ACCEPT = "accept"

    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { getAccessTokenUseCase() }
        val originalRequest = chain.request()

        val request = originalRequest.newBuilder()
            .addHeader(HEADER_ACCEPT, "application/hal+json")
            .addHeader(HEADER_CONTENT_TYPE, "application/json")
            .addHeader(HEADER_AUTHORIZATION, "Bearer $accessToken")
            .build()

        Timber.d("AccessToken 헤더 추가됨: $accessToken")

        return chain.proceed(request)
    }
}