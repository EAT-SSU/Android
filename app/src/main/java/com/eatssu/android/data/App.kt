package com.eatssu.android.data

import android.app.Application


class App: Application() {
    companion object{
        lateinit var token_prefs : TokenSharedPreferences
    }

    override fun onCreate() {
        token_prefs = TokenSharedPreferences(applicationContext)
        super.onCreate()
    }
}