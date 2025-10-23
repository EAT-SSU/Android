package com.eatssu.android.di


import com.eatssu.android.BuildConfig
import com.eatssu.android.BuildConfig.BASE_URL
import com.eatssu.android.di.network.ApiResultCallAdapterFactory
import com.eatssu.android.di.network.TokenAuthenticator
import com.eatssu.android.di.network.TokenInterceptor
import com.eatssu.android.domain.usecase.auth.GetRefreshTokenUseCase
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.ReissueTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.SetRefreshTokenUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Qualifier
import javax.inject.Singleton

class NullOnEmptyConverterFactory : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *> {
        val delegate: Converter<ResponseBody, Any> =
            retrofit.nextResponseBodyConverter(this, type, annotations)
        return Converter { body -> if (body.contentLength() == 0L) null else delegate.convert(body) }
    }
}

/** retrofit, okhttpClient에 토큰이 필요하지 않음을 명시하기 위한 Qualifier */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoToken

/**
 * NetworkModule : Retrofit과 OkHttpClient를 제공하는 모듈
 * - OkHttpClient : API 요청 시 AccessToken을 헤더에 추가하는 인터셉터와 로깅 인터셉터를 사용
 * - Retrofit : OkHttpClient를 사용하여 API 요청을 처리
 * */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 토큰이 필요한 okhttpClient
    @Singleton
    @Provides
    fun provideAuthOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ) = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    } else {
        // 프로덕션 환경에서는 로깅 인터셉터를 추가하지 않음
        OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    // 토큰 없는 OkHttpClient (로그인/회원가입/토큰 재발급용)
    @Singleton
    @Provides
    @NoToken
    fun provideNoAuthOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideCallAdapterFactory(): CallAdapter.Factory = ApiResultCallAdapterFactory()

    // 토큰이 필요한 retrofit
    @Singleton
    @Provides
    fun provideAuthRetrofit(
        okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
    ): Retrofit {
        return Retrofit.Builder().client(okHttpClient).baseUrl(BASE_URL)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .build()
    }

    // 토큰 없는 retrofit
    @Singleton
    @Provides
    @NoToken
    fun provideNoAuthRetrofit(
        @NoToken okHttpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
    ): Retrofit {
        return Retrofit.Builder().client(okHttpClient).baseUrl(BASE_URL)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        getRefreshTokenUseCase: GetRefreshTokenUseCase,
        setAccessTokenUseCase: SetAccessTokenUseCase,
        setRefreshTokenUseCase: SetRefreshTokenUseCase,
        reissueTokenUseCase: ReissueTokenUseCase,
        logoutUseCase: LogoutUseCase,
    ): TokenAuthenticator {
        return TokenAuthenticator(
            getRefreshTokenUseCase,
            setAccessTokenUseCase,
            setRefreshTokenUseCase,
            reissueTokenUseCase,
            logoutUseCase,
        )
    }
}