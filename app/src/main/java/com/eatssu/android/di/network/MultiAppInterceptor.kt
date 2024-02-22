package com.eatssu.android.di.network

import com.eatssu.android.App
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Singleton


@Singleton
class MultiAppInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
        val requestBuilder = request().newBuilder()
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("Authorization", "Bearer ${App.token_prefs.accessToken}")

        val newRequest = requestBuilder.build()
        proceed(newRequest)
    }
}