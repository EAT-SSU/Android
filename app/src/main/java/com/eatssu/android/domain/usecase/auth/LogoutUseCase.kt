package com.eatssu.android.domain.usecase.auth

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.eatssu.android.data.MySharedPreferences
import com.eatssu.android.presentation.login.LoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke() {
        MySharedPreferences.clearUser(context)
    }
}