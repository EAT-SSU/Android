package com.eatssu.android.domain.usecase.widget

import com.eatssu.android.data.local.WidgetDataStore
import com.eatssu.common.enums.Restaurant
import javax.inject.Inject

class SaveRestaurantByFileKeyUseCase @Inject constructor(
    private val widgetPrefsRepository: WidgetDataStore
) {
    suspend operator fun invoke(fileKey: String, restaurant: Restaurant) {
        widgetPrefsRepository.saveRestaurantByFileKey(fileKey, restaurant)
    }
} 