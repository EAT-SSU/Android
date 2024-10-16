package com.eatssu.android.domain.usecase

import android.content.Context
import com.eatssu.android.data.db.MySharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SetAccessTokenUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(accessToken: String) {
        MySharedPreferences.setAccessToken(context, accessToken)
    }
}