package com.eatssu.android.domain.model

import com.eatssu.android.data.enums.Restaurant


sealed interface WidgetMealInfo {
    object Loading : WidgetMealInfo

    data class Available(
        val mealTime: String,
        val mealList: List<List<String>>,
        val restaurant: Restaurant,
    ) : WidgetMealInfo

    object Unavailable : WidgetMealInfo
}