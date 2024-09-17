package com.eatssu.android.data.usecase

import com.eatssu.android.App
import com.eatssu.android.util.MySharedPreferences
import javax.inject.Inject

class SetNotificationStatusUseCase @Inject constructor(
//    private val preferencesRepository: PreferencesRepository,
) {
    suspend operator fun invoke(isAlarmOn: Boolean) {

        return MySharedPreferences.setDailyNotification(App.appContext, isAlarmOn)
    }
}