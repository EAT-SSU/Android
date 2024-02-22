package com.eatssu.android.di.network

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.eatssu.android.App
import com.eatssu.android.ui.login.LoginActivity
import com.eatssu.android.util.MySharedPreferences
import com.eatssu.android.util.TokenManager
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Singleton


class AppInterceptor(val context: Context) : Interceptor {
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
                        val intent = Intent(context, LoginActivity::class.java) // 로그인 화면으로 이동
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(intent)
                    }
                }
            }
        }
        response
    }
}