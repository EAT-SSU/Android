package com.eatssu.android.data.usecase

import com.eatssu.android.data.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyNotificationStatusUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return preferencesRepository.dailyNotificationStatus
    }
}
