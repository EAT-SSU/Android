package com.eatssu.android.domain.usecase.widget

import com.eatssu.android.data.repository.WidgetPreferencesRepository
import javax.inject.Inject

class SaveRestaurantByFileKeyUseCase @Inject constructor(
    private val widgetPrefsRepository: WidgetPreferencesRepository
) {
    suspend operator fun invoke(fileKey: String, restaurantDisplayName: String) {
        widgetPrefsRepository.saveRestaurantByFileKey(fileKey, restaurantDisplayName)
    }
} 