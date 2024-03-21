package com.eatssu.android.data.usecase

import android.util.Log
import com.eatssu.android.App
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(): String? {
        val token = App.token_prefs.accessToken
        Log.d("accessToken", "token: $token")
        return token
    }
}