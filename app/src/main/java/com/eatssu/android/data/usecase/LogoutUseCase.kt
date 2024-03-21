package com.eatssu.android.data.usecase

import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
//    private val setAccessTokenUseCase: SetAccessTokenUseCase,
//    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
//    private val credentialManager: CredentialManager,
) {
    suspend operator fun invoke() {
        App.token_prefs.accessToken = ""
        App.token_prefs.refreshToken = ""

        MySharedPreferences.clearUser(App.appContext)
        App.token_prefs.clearTokens()

//        setAccessTokenUseCase(null)
//        setRefreshTokenUseCase(null)
//        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}