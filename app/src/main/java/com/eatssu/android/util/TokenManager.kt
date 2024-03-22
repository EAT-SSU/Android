package com.eatssu.android.util

import android.util.Log
import com.eatssu.android.App
import com.eatssu.android.data.service.OauthService

object TokenManager {
    private val oauthService = RetrofitImpl.nonRetrofit.create(OauthService::class.java)

    fun refreshToken(): String {
        val refreshToken = "Bearer " + MySharedPreferences.getRefreshToken(App.appContext)
        val response = oauthService.getNewToken(refreshToken).execute()
        if (response.code() == 200) {
            Log.d("tokenManager", "토큰 다시 나옴")
            response.body()?.result?.let { it ->
                MySharedPreferences.setAccessToken(App.appContext, it.accessToken)
                MySharedPreferences.setRefreshToken(App.appContext, it.refreshToken)

                return it.accessToken
            }
        }
        throw IllegalStateException("토큰 재발급 실패")
    }

}
