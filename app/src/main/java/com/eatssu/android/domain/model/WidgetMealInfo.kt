package com.eatssu.android.domain.model

import com.eatssu.android.data.enums.Restaurant
import kotlinx.serialization.Serializable

@Serializable
sealed interface WidgetMealInfo {
    @Serializable
    object Loading : WidgetMealInfo

    @Serializable
    data class Available(
        val mealTime: String,
        val mealList: List<List<String>>,
        val restaurant: Restaurant,
    ) : WidgetMealInfo

    @Serializable
    object Unavailable : WidgetMealInfo
}