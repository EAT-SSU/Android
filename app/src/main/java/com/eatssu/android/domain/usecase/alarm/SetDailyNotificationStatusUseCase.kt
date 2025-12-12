package com.eatssu.android.domain.usecase.alarm

import com.eatssu.android.data.local.SettingDataStore
import javax.inject.Inject


class SetDailyNotificationStatusUseCase @Inject constructor(
    private val settingDataStore: SettingDataStore
) {
    suspend operator fun invoke(status: Boolean) {
        settingDataStore.setDailyNotificationStatus(status)
    }
}
