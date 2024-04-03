package com.eatssu.android.di.network


import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.eatssu.android.App
import com.eatssu.android.BuildConfig.BASE_URL
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.TokenResponse
import com.eatssu.android.data.usecase.GetAccessTokenUseCase
import com.eatssu.android.data.usecase.GetRefreshTokenUseCase
import com.eatssu.android.data.usecase.LogoutUseCase
import com.eatssu.android.data.usecase.SetAccessTokenUseCase
import com.eatssu.android.data.usecase.SetRefreshTokenUseCase
import com.eatssu.android.ui.login.LoginActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber
import java.lang.reflect.Type
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
    private val logoutUseCase: LogoutUseCase,
) : Interceptor {

    companion object {
        val EXCEPT_LIST = listOf(
            "/oauths/reissue/token",
            "/oauths/kakao",
        )

        const val TAG = "TokenInterceptor"

        private const val CODE_TOKEN_EXPIRED = 401
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_ACCESS_TOKEN = "X-ACCESS-AUTH"
        private const val HEADER_REFRESH_TOKEN = "X-REFRESH-AUTH"
    }

    private lateinit var newAccessToken: String
    private lateinit var newRefreshToken: String

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { getAccessTokenUseCase() }
        val refreshToken = runBlocking { getRefreshTokenUseCase() }

        val originalRequest = chain.request()
        val request = chain.request().newBuilder().apply {
            if (EXCEPT_LIST.none { originalRequest.url.encodedPath.endsWith(it) }) {
                addHeader("accept", "application/hal+json")
                addHeader("Content-Type", "application/json")
                addHeader(HEADER_AUTHORIZATION, "Bearer $accessToken")
            }
        }.build()

        val response = chain.proceed(request)

        if (response.code == 401) {
            Log.d(TAG, "토큰 퉤퉤")
            response.close()

            try {
                val refreshTokenRequest = originalRequest.newBuilder()
                    .post("".toRequestBody())
                    .url("$BASE_URL/oauths/reissue/token")
                    .addHeader(HEADER_AUTHORIZATION, "Bearer $refreshToken")
                    .build()

                Log.d(TAG, "재발급 중")

                val refreshTokenResponse = chain.proceed(refreshTokenRequest)
                Log.d(TAG, "refreshTokenResponse : $refreshTokenResponse")

                if (refreshTokenResponse.isSuccessful) {
                    Log.d(TAG, "재발급 성공")

                    val responseToken = parseRefreshTokenResponse(refreshTokenResponse)

                    responseToken?.result?.let {
                        runBlocking {
                            setAccessTokenUseCase(it.accessToken)
                            setRefreshTokenUseCase(it.refreshToken)

                            newAccessToken = it.accessToken
                        }
                    }

                    refreshTokenResponse.close()
                    val newRequest = originalRequest.newAuthBuilder().build()
                    return chain.proceed(newRequest)
                }

            } catch (e: Exception) {
                runBlocking { logoutUseCase() }
                Timber.tag(TAG).d("재발급 실패 " + e)

                Handler(Looper.getMainLooper()).post {
                    val context = App.appContext
                    Toast.makeText(context, "토큰이 만료되어 로그아웃 됩니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, LoginActivity::class.java) // 로그인 화면으로 이동
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context.startActivity(intent)
                }
            }
        }

        if (response.code == 404) {
            runBlocking { logoutUseCase() }
            Timber.e("404 + 다른 유저!")

            Handler(Looper.getMainLooper()).post {
                val context = App.appContext
                Toast.makeText(context, "토큰이 만료되어 로그아웃 됩니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, LoginActivity::class.java) // 로그인 화면으로 이동
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
            }
        }

        if (response.code == 500) {
            runBlocking { logoutUseCase() }
            Timber.e("500 + 다른 유저")

            Handler(Looper.getMainLooper()).post {
                val context = App.appContext
                Toast.makeText(context, "토큰이 만료되어 로그아웃 됩니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, LoginActivity::class.java) // 로그인 화면으로 이동
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                context.startActivity(intent)
            }
        }

        return response
    }

    private fun Request.newAuthBuilder() =
        this.newBuilder().addHeader(HEADER_AUTHORIZATION, "Bearer $newAccessToken")

    private fun parseRefreshTokenResponse(response: Response): BaseResponse<TokenResponse>? {
        return try {
            val gson = Gson()
            val responseType: Type = object : TypeToken<BaseResponse<TokenResponse>>() {}.type
            gson.fromJson(response.body?.string(), responseType)
        } catch (e: Exception) {
            null
        }
    }
}