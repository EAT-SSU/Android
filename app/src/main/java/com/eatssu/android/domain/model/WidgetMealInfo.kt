package com.eatssu.android.domain.model

import com.eatssu.common.enums.Restaurant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface WidgetMealInfo {
    @Serializable
    @SerialName("Loading")
    object Loading : WidgetMealInfo

    @Serializable
    @SerialName("Available")
    data class Available(
        val breakfast: List<List<String>>,
        val lunch: List<List<String>>,
        val dinner: List<List<String>>,
        val restaurant: Restaurant,
    ) : WidgetMealInfo

    @Serializable
    @SerialName("Unavailable")
    object Unavailable : WidgetMealInfo
}
