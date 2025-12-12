package com.eatssu.android.domain.usecase.widget

import com.eatssu.android.data.local.WidgetDataStore
import com.eatssu.common.enums.Restaurant
import javax.inject.Inject

class LoadRestaurantByFileKeyUseCase @Inject constructor(
    private val widgetPrefsRepository: WidgetDataStore
) {
    suspend operator fun invoke(fileKey: String): Restaurant? {
        return widgetPrefsRepository.loadRestaurantByFileKey(fileKey)
    }
} 