package com.eatssu.android.data

import com.eatssu.android.App
import com.eatssu.android.data.RetrofitImpl.retrofit
import com.eatssu.android.data.service.UserService

object RefreshTokenService {

    private val userService = retrofit.create(UserService::class.java)

    fun refreshToken(): String {
        val res = userService.getNewToken().execute()
        if (res.isSuccessful) {
            val newAccessToken = res.body()?.accessToken ?: ""
            val newRefreshToken = res.body()?.refreshToken ?: ""
            if (newAccessToken.isNotBlank()) {
                App.token_prefs.accessToken = newAccessToken
                App.token_prefs.refreshToken = newRefreshToken
                return newAccessToken
            }
        }
        throw IllegalStateException("토큰 재발급 실패")
    }
}