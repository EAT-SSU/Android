package com.eatssu.android.data.usecase.auth

import android.content.Context
import com.eatssu.android.util.MySharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SetUserEmailUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(email: String) {
        MySharedPreferences.setUserEmail(context, email)
    }
}