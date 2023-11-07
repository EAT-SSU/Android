package com.eatssu.android.data

import com.eatssu.android.App
import com.eatssu.android.data.service.UserService

object TokenManager {
    private val userService = RetrofitImpl.retrofit.create(UserService::class.java)

    fun refreshToken(): String {
        val response = userService.getNewToken().execute()
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.accessToken.isNotBlank()) {
                    App.token_prefs.accessToken = it.accessToken
                    App.token_prefs.refreshToken = it.refreshToken
                    return it.accessToken
                }
            }
        }
        throw IllegalStateException("토큰 재발급 실패")
    }
}