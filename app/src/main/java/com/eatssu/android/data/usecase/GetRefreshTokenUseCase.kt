package com.eatssu.android.data.usecase

import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class GetRefreshTokenUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(): String {

        return MySharedPreferences.getRefreshToken(App.appContext)
    }
}