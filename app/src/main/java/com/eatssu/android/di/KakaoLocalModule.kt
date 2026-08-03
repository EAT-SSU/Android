package com.eatssu.android.di

import javax.inject.Singleton
import com.eatssu.android.data.remote.service.KakaoLocalService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object KakaoLocalModule {
    @Provides
    @Singleton
    fun provideKakaoLocalService(json: Json): KakaoLocalService =
        Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(KakaoLocalService::class.java)
}
