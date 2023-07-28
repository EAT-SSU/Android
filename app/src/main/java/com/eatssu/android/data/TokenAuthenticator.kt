package com.eatssu.android.data

import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator: Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.i("Authenticator", response.toString())
        Log.i("Authenticator", "토큰 재발급 시도")
        return try {
            val newAccessToken = RefreshTokenService.refreshToken()
            Log.i("Authenticator", "토큰 재발급 성공 : $newAccessToken")
            response.request.newBuilder()
                .removeHeader("Bearer").apply {
                    addHeader("Bearer", newAccessToken)
                }.build() // 토큰 재발급이 성공했다면, 기존 헤더를 지우고, 새로운 해더를 단다.
        } catch (e: Exception) {
            e.printStackTrace()
            null // 만약 토큰 재발급이 실패했다면 헤더에 아무것도 추가하지 않는다.
        }
    }
}