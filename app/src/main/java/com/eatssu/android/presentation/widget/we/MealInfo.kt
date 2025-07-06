package com.eatssu.android.presentation.widget.we

import com.eatssu.android.data.enums.Restaurant
import kotlinx.serialization.Serializable

@Serializable
sealed interface MealInfo {
    @Serializable
    object Loading : MealInfo

    @Serializable
    data class Available(
        val mealTime: String,
        val mealList: List<List<String>>,
        val restaurant: Restaurant,
    ) : MealInfo

    @Serializable
    object Unavailable : MealInfo
}