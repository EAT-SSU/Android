package com.eatssu.android.domain.model

import com.eatssu.android.data.enums.Restaurant


sealed interface WidgetMealInfo {
    object Loading : WidgetMealInfo

    data class Available(
        val breakfast: List<List<String>>,
        val lunch: List<List<String>>,
        val dinner: List<List<String>>,
        val restaurant: Restaurant,
    ) : WidgetMealInfo

    object Unavailable : WidgetMealInfo
}