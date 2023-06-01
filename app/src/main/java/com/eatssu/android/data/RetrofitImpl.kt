import com.eatssu.android.App
import com.eatssu.android.BuildConfig
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object RetrofitImpl {
    private const val BASE_URL = BuildConfig.BASE_URL
    private val nonOkHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(NonAppInterceptor())
            .build()
    }

    val nonRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(nonOkHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    class NonAppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val requestBuilder = request().newBuilder()
                .addHeader("accept", "application/hal+json")
                .addHeader("Content-Type", "application/json")
//                .addHeader("Authorization", "Bearer ${App.token_prefs.accessToken}")

//            App.token_prefs.accessToken?.let { accessToken ->
//                requestBuilder.addHeader("Authorization", "Bearer $accessToken")
//            }

            val newRequest = requestBuilder.build()
            proceed(newRequest)
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(AppInterceptor())
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    class AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val requestBuilder = request().newBuilder()
                .addHeader("accept", "application/hal+json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${App.token_prefs.accessToken}")

//            App.token_prefs.accessToken?.let { accessToken ->
//                requestBuilder.addHeader("Authorization", "Bearer $accessToken")
//            }

            val newRequest = requestBuilder.build()
            proceed(newRequest)
        }
    }


    private val mOkHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(mAppInterceptor())
            .build()
    }

    val mRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(mOkHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    class mAppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val requestBuilder = request().newBuilder()
//                .addHeader("accept", "application/hal+json")
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader("Authorization", "Bearer ${App.token_prefs.accessToken}")

//            App.token_prefs.accessToken?.let { accessToken ->
//                requestBuilder.addHeader("Authorization", "Bearer $accessToken")
//            }

            val newRequest = requestBuilder.build()
            proceed(newRequest)
        }
    }
}
