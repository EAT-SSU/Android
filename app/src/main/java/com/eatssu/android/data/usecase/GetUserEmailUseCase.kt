package com.eatssu.android.data.usecase

import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class GetUserEmailUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(): String {

        return MySharedPreferences.getUserEmail(App.appContext)
    }
}