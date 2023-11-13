package com.eatssu.android.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.eatssu.android.App
import com.eatssu.android.data.service.UserService

object TokenManager {
    @RequiresApi(Build.VERSION_CODES.M)
    private val userService = RetrofitImpl.nonRetrofit.create(UserService::class.java)

    @RequiresApi(Build.VERSION_CODES.M)
    fun refreshToken(): String {
        val refreshToken = "Bearer "+App.token_prefs.refreshToken
        val response = userService.getNewToken(refreshToken).execute()
        if (response.code()==200) {
            Log.d("tokenManager","토큰 다시 나옴")
            response.body()?.let {
                if (it.accessToken.isNotBlank()) {
                    App.token_prefs.accessToken = it.accessToken
                    App.token_prefs.refreshToken = it.refreshToken
                    return it.accessToken
                }
            }
        }
        Log.d("tokenManager","토큰 다시 안안안안나옴")
        throw IllegalStateException("토큰 재발급 실패")
    }
}