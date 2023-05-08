package com.eatssu.android

import android.app.Application
import com.eatssu.android.data.TokenSharedPreferences


class App: Application() {
    companion object{
        lateinit var token_prefs : TokenSharedPreferences
    }

    override fun onCreate() {
        token_prefs = TokenSharedPreferences(applicationContext)
        super.onCreate()
    }
}