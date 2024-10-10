package com.eatssu.android.domain.usecase

import android.content.Context
import com.eatssu.android.data.db.MySharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke() {

        MySharedPreferences.clearUser(context)

    }
}