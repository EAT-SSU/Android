package com.eatssu.android.domain.usecase.widget

import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.repository.WidgetPreferencesRepository
import javax.inject.Inject

class LoadRestaurantByFileKeyUseCase @Inject constructor(
    private val widgetPrefsRepository: WidgetPreferencesRepository
) {
    suspend operator fun invoke(fileKey: String): Restaurant? {
        return widgetPrefsRepository.loadRestaurantByFileKey(fileKey)
    }
} 