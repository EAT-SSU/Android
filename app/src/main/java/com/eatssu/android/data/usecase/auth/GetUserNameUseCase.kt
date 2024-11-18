package com.eatssu.android.data.usecase.auth

import android.content.Context
import com.eatssu.android.util.MySharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetUserNameUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(): String {
        return MySharedPreferences.getUserName(context)
    }
}