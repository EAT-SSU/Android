package com.eatssu.android.data.usecase

import android.util.Log
import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class SetRefreshTokenUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(refreshToken: String) {
        MySharedPreferences.setRefreshToken(App.appContext, refreshToken)

        Log.d("accessToken", "로그아웃")

    }
}