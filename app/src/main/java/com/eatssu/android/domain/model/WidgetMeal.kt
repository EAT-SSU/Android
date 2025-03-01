package com.eatssu.android.domain.model

data class WidgetMeal(
    val restaurantName: String,
    val date: String,
    val time: String,
    val menuList: List<String>
)
