package com.eatssu.android.data

import android.util.Log
import com.eatssu.android.App
import com.eatssu.android.BuildConfig
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
//            .authenticator(TokenAuthenticator())
            .build()
    }

    private fun createMultiPartOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(mAppInterceptor())
//            .authenticator(TokenAuthenticator())
            .build()
    }

    private class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            var response: Response
            val originalRequest = request()
            val requestBuilder = originalRequest.newBuilder()
                .addHeader("accept", "application/hal+json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${App.token_prefs.accessToken}")

            val request = requestBuilder.build()
            response = proceed(request)

            // Unauthorized (401) 상태 코드를 받았을 경우 토큰 재발급 시도
            if (response.code == 401 or 403) {
                response.close()

                Log.d("AppInterceptor", "토큰 재발급 시도")

                try {
                    val newAccessToken = TokenManager.refreshToken()
                    Log.d("AppInterceptor", "토큰 재발급 성공 : $newAccessToken")

                    // 재발급 받은 토큰으로 새로운 요청 생성
                    val newRequest = request.newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()

                    // 새로운 요청으로 다시 시도
                    response = proceed(newRequest)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            response
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
