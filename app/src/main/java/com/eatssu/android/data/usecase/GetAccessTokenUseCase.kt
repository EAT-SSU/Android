package com.eatssu.android.data.usecase

import android.content.Context
import com.eatssu.android.util.MySharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(): String {
        return MySharedPreferences.getAccessToken(context)
    }
}