package com.eatssu.android

import android.app.Application
import android.content.Context
import com.eatssu.android.data.TokenSharedPreferences
import com.kakao.sdk.common.KakaoSdk


class App: Application() {
    companion object{
        var appContext : Context? = null
        lateinit var token_prefs : TokenSharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        token_prefs = TokenSharedPreferences(applicationContext)


        appContext = this
        KakaoSdk.init(this,getString(R.string.kakao_app_key))

    }
}