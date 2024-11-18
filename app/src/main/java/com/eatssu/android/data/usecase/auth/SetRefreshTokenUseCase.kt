package com.eatssu.android.data.usecase.auth

import android.content.Context
import com.eatssu.android.util.MySharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SetRefreshTokenUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(refreshToken: String) {
        MySharedPreferences.setRefreshToken(context, refreshToken)
    }
}