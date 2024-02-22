package com.eatssu.android.di

import com.eatssu.android.App
import com.eatssu.android.di.network.AppInterceptor
import com.eatssu.android.di.network.CacheInterceptor
import com.eatssu.android.di.network.MultiAppInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MultiClient

    @Provides
    @Singleton
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }


    @Provides
    @Singleton
    fun provideAppInterceptor(): AppInterceptor {
        return AppInterceptor(App.appContext)
    }

    @Singleton
    @Provides
    fun provideClient(//일반
//        authorizationInterceptor: Interceptor,
//        tokenAuthenticator: Authenticator,
//        cacheInterceptor: CacheInterceptor
        appInterceptor: AppInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(appInterceptor)

//            addInterceptor(cacheInterceptor)
//            addInterceptor(authorizationInterceptor)
        }
//        builder.authenticator(tokenAuthenticator)
        return builder.build()
    }

    @AuthClient
    @Singleton
    @Provides
    fun provideAuthClient( //이게 토큰 없는거
//        authorizationInterceptor: Interceptor,
        appInterceptor: AppInterceptor,
//        cacheInterceptor: CacheInterceptor
        ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.apply {
            addInterceptor(loggingInterceptor)
//            addInterceptor(appInterceptor)
//            addInterceptor(cacheInterceptor)
//            addInterceptor(authorizationInterceptor)
        }
        return builder.build()
    }


    @MultiClient
    @Singleton
    @Provides
    fun provideMultiClient(
//        authorizationInterceptor: Interceptor,
        multiAppInterceptor: MultiAppInterceptor,
//        cacheInterceptor: CacheInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.apply {
            addInterceptor(loggingInterceptor)
            addInterceptor(multiAppInterceptor)
//            addInterceptor(cacheInterceptor)
//            addInterceptor(authorizationInterceptor)
        }
        return builder.build()
    }

}
