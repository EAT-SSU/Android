package com.eatssu.android

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App: Application() {
    companion object{
        lateinit var appContext : Context
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        appContext = this
        KakaoSdk.init(this,BuildConfig.KAKAO_NATIVE_APP_KEY)

        Timber.plant(Timber.DebugTree())
    }
}