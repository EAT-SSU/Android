package com.eatssu.android.di.network

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.eatssu.android.App
import com.eatssu.android.data.usecase.GetAccessTokenUseCase
import com.eatssu.android.data.usecase.LogoutUseCase
import com.eatssu.android.ui.login.LoginActivity
import com.eatssu.android.util.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
) : Interceptor {

    companion object {
        val EXCEPT_LIST = listOf(
            "/login/google", "/reissue", "/users",
            "/",

            "/oauths/reissue/token",
            "/oauths/kakao",

            "/oauths/**", "/users/**",
            "/menus/**", "/meals/**", "/restaurants/**", "/reviews/**",
            "/inquiries/{userInquiriesId}",
            "/inquiries/list",
            "/admin/login"
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { getAccessTokenUseCase() }
        val originalRequest = chain.request()
        val request = chain.request().newBuilder().apply {
            if (EXCEPT_LIST.none { originalRequest.url.encodedPath.endsWith(it) }) {
                addHeader("accept", "application/hal+json")
                addHeader("Content-Type", "application/json")
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        val response = chain.proceed(request)
        if (response.code == 401) {
//        if (response.code == 401 && !response.request.url.toString().contains("reissue")) {
            // refresh token
            response.close()

            Log.d("AppInterceptor", "토큰 재발급 시도")
            try {

                val newAccessToken = TokenManager.refreshToken()
                // 재발급 받은 토큰으로 새로운 요청 생성
                val newRequest = request.newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", "Bearer $newAccessToken")
                    .build()

                chain.proceed(newRequest)
            } catch (e: Exception) {
                Log.d("AppInterceptor", "토큰 재발급 실패" + response)
//                    e.printStackTrace() //이거 빼니까 강제종료 안된다

                // 리프레시 토큰이 만료되어 재발급에 실패했을 때 로그아웃 처리
                if (response.code == 403) {
                    runBlocking { logoutUseCase() }

                    Handler(Looper.getMainLooper()).post {
                        val context = App.appContext
                        Toast.makeText(context, "토큰이 만료되어 로그아웃 됩니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, LoginActivity::class.java) // 로그인 화면으로 이동
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        context.startActivity(intent)
                    }
                }
            }

        }
        return response
    }
}