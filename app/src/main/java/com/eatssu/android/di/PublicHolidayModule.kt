package com.eatssu.android.di

import com.eatssu.android.BuildConfig
import com.eatssu.android.data.remote.repository.PublicHolidayRepositoryImpl
import com.eatssu.android.data.remote.service.PublicHolidayService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PublicHolidayApi

@Module
@InstallIn(SingletonComponent::class)
object PublicHolidayModule {

    private const val PUBLIC_HOLIDAY_BASE_URL = "https://apis.data.go.kr/"

    @Provides
    @Singleton
    @Named(PublicHolidayRepositoryImpl.PUBLIC_HOLIDAY_SERVICE_KEY_NAME)
    fun providePublicHolidayServiceKey(): String {
        return BuildConfig.HOLIDAY_API_KEY
    }

    @Provides
    @Singleton
    @PublicHolidayApi
    fun providePublicHolidayRetrofit(
        @NoToken okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(PUBLIC_HOLIDAY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun providePublicHolidayService(
        @PublicHolidayApi retrofit: Retrofit,
    ): PublicHolidayService {
        return retrofit.create(PublicHolidayService::class.java)
    }
}
