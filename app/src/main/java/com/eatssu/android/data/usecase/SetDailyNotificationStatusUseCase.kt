package com.eatssu.android.data.usecase

import com.eatssu.android.data.repository.PreferencesRepository
import javax.inject.Inject


class SetDailyNotificationStatusUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(status: Boolean) {
        preferencesRepository.setDailyNotificationStatus(status)
    }
}
