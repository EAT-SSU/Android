package com.eatssu.android.data.usecase

import android.util.Log
import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class SetAccessTokenUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke() {
        App.token_prefs.accessToken = ""
        App.token_prefs.refreshToken = ""

        MySharedPreferences.clearUser(App.appContext)

//        val token = App.token_prefs.accessToken
        Log.d("accessToken", "로그아웃")

    }
}