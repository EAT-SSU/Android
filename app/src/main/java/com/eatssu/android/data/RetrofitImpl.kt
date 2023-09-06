package com.eatssu.android.data

import com.eatssu.android.App
import com.eatssu.android.BuildConfig
import com.eatssu.android.data.TokenAuthenticator
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object RetrofitImpl {
    private const val BASE_URL = BuildConfig.BASE_URL

    // 공통으로 사용하는 OkHttpClient 생성
    private val commonOkHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    // 토큰 없는 Retrofit
    val nonRetrofit: Retrofit by lazy {
        createRetrofit(commonOkHttpClient)
    }

    // 토큰이 있는 Retrofit
    val retrofit: Retrofit by lazy {
        createRetrofit(createTokenOkHttpClient())
    }

    // 멀티파트 레트로핏
    val mRetrofit: Retrofit by lazy {
        createRetrofit(createMultiPartOkHttpClient())
    }

    private fun createRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(BASE_URL)
            .build()
    }

    private fun createTokenOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AppInterceptor())
            .authenticator(TokenAuthenticator())
            .build()
    }

    private fun createMultiPartOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(mAppInterceptor())
            .authenticator(TokenAuthenticator())
            .build()
    }

    private class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val requestBuilder = request().newBuilder()
                .addHeader("accept", "application/hal+json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${App.token_prefs.accessToken}")

            val newRequest = requestBuilder.build()
            proceed(newRequest)
        }
    }

    private class mAppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val requestBuilder = request().newBuilder()
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("Authorization", "Bearer ${App.token_prefs.accessToken}")

            val newRequest = requestBuilder.build()
            proceed(newRequest)
        }
    }
}
