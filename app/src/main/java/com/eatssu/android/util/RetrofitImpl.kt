package com.eatssu.android.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.eatssu.android.App
import com.eatssu.android.BuildConfig
import com.eatssu.android.ui.login.SocialLoginActivity
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object RetrofitImpl {
    private const val BASE_URL = BuildConfig.BASE_URL

    val size = 10 * 1024 * 1024 // 10MB Cache size

    val mCache = Cache(App.appContext.cacheDir, size.toLong())

    val cacheInterceptor = Interceptor{ chain ->
        var request = chain.request()
        request = if (hasNetwork(App.appContext))
            request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
        else
            request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
        chain.proceed(request)
    }

    // Check if network is available
    fun hasNetwork(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null
    }


    // 공통으로 사용하는 OkHttpClient 생성
    private val commonOkHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(cacheInterceptor)
            .cache(mCache)
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
        return commonOkHttpClient.newBuilder()
            .addInterceptor(AppInterceptor(App.appContext))
            .build()
    }

    private fun createMultiPartOkHttpClient(): OkHttpClient {
        return commonOkHttpClient.newBuilder()
            .addInterceptor(mAppInterceptor())
            .build()
    }

    private class AppInterceptor(val context: Context) : Interceptor {
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
                    Log.d("AppInterceptor", "토큰 재발급 실패"+response)
//                    e.printStackTrace() //이거 빼니까 강제종료 안된다
                    // 리프레시 토큰이 만료되어 재발급에 실패했을 때 로그아웃 처리
                    if (response.code == 403) {
                        MySharedPreferences.clearUser(context) //자동 로그인
                        App.token_prefs.clearTokens() // 토큰 제거
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, "토큰이 만료되어 로그아웃 됩니다.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, SocialLoginActivity::class.java) // 로그인 화면으로 이동
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            context.startActivity(intent)
                        }
                    }
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
