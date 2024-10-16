package com.eatssu.android.domain.usecase

import android.content.Context
import com.eatssu.android.data.db.MySharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetRefreshTokenUseCase @Inject constructor(
    @ApplicationContext private val context: Context
//    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(): String {
        return MySharedPreferences.getRefreshToken(context)
    }
}