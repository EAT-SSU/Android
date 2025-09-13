package com.eatssu.android.domain.usecase.widget

import com.eatssu.android.data.repository.WidgetPreferencesRepository
import com.eatssu.common.enums.Restaurant
import javax.inject.Inject

class SaveRestaurantByFileKeyUseCase @Inject constructor(
    private val widgetPrefsRepository: WidgetPreferencesRepository
) {
    suspend operator fun invoke(fileKey: String, restaurant: Restaurant) {
        widgetPrefsRepository.saveRestaurantByFileKey(fileKey, restaurant)
    }
} 