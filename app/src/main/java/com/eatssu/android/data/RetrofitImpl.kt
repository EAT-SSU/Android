package com.eatssu.android.data


import com.eatssu.android.App
import com.eatssu.android.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import okhttp3.Headers

object RetrofitImpl {
    private const val BASE_URL = BuildConfig.BASE_URL

    private fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    fun getApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    class AppInterceptor : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val headers = Headers.Builder()
                .add("accept", "application/hal+json")
                .apply {
                    if (App.token_prefs.accessToken != null) {
                        add("Authorization", "Bearer ${App.token_prefs.accessToken.toString()}")
                    }
                }
                .add("Content-Type", "application/json")
                .build()

            val newRequest = request().newBuilder()
                .headers(headers)
                .build()
            proceed(newRequest)
        }
    }
}