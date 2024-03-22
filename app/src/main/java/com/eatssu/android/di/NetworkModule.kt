package com.eatssu.android.di


import com.eatssu.android.BuildConfig
import com.eatssu.android.data.service.OauthService
import com.eatssu.android.di.network.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
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

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = BuildConfig.BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
    ) = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder().addInterceptor(loggingInterceptor).addInterceptor(tokenInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().client(okHttpClient).baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .build()
    }


    @Provides
    @Singleton
    fun provideOauthService(retrofit: Retrofit): OauthService {
        return retrofit.create(OauthService::class.java)
    }

    /*

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
*/
}