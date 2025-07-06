package com.eatssu.android.presentation.widget.we

import kotlinx.serialization.Serializable

@Serializable
sealed interface MealInfo {
    @Serializable
    object Loading : MealInfo

    @Serializable
    data class Available(
        val mealTime: String,
        val mealList: List<List<String>>,
    ) : MealInfo

    @Serializable
    object Unavailable : MealInfo
}