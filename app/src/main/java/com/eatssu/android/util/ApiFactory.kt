package com.eatssu.android.util

import com.eatssu.android.BuildConfig
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.data.service.OauthService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiFactory {
    private val json by lazy {
        Json {
            coerceInputValues = true
            ignoreUnknownKeys = true
        }
    }
    private val client by lazy {
        OkHttpClient.Builder()
//            .addInterceptor(TokenManager())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }).build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType())).build()
    }

    inline fun <reified T> create(): T = retrofit.create(T::class.java)

    object ServicePool {
        val oauthService = create<OauthService>()
        val menuService = create<MenuService>()
//        val homeService = create<HomeService>()
//        val myWordService = create<MyWordService>()
//        val chapterService = create<ChapterService>()
//        val wordService = create<WordService>()
    }
}