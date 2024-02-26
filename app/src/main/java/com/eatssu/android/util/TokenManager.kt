package com.eatssu.android.util

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.eatssu.android.App
import com.eatssu.android.data.service.OauthService
import com.eatssu.android.data.service.UserService

object TokenManager {
    private val oauthService = RetrofitImpl.nonRetrofit.create(OauthService::class.java)

    fun refreshToken():String {
        val refreshToken = "Bearer "+App.token_prefs.refreshToken
        val response = oauthService.getNewToken(refreshToken).execute()
        if (response.code()==200) {
            Log.d("tokenManager","토큰 다시 나옴")
            response.body()?.result.let { it ->
                App.token_prefs.accessToken = it!!.accessToken
                    App.token_prefs.refreshToken = it.refreshToken
                    return it.accessToken
                }
            }
        throw IllegalStateException("토큰 재발급 실패")
        }

    }
