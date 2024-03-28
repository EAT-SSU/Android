package com.eatssu.android.data.usecase

import android.util.Log
import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class SetAccessTokenUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(accessToken: String) {
        MySharedPreferences.setAccessToken(App.appContext, accessToken)

        Log.d("SetAccessTokenUseCase", accessToken)

    }
}