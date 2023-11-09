package com.eatssu.android

import android.app.Application
import android.content.Context
import com.eatssu.android.data.TokenSharedPreferences
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk


class App: Application() {
    companion object{
        lateinit var appContext : Context
        lateinit var token_prefs : TokenSharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        token_prefs = TokenSharedPreferences(applicationContext)

        appContext = this
        KakaoSdk.init(this,BuildConfig.KAKAO_NATIVE_APP_KEY)

    }
}