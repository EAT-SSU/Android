package com.eatssu.android.domain.usecase

import android.content.Context
import com.eatssu.android.data.db.MySharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetUserEmailUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(): String {

        return MySharedPreferences.getUserEmail(context)
    }
}