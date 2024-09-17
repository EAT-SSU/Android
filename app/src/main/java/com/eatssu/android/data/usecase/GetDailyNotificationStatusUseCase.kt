package com.eatssu.android.data.usecase

import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class GetDailyNotificationStatusUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(): Boolean {

        return MySharedPreferences.getDailyNotification(App.appContext)
    }
}