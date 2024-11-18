package com.eatssu.android.domain.model

import com.eatssu.android.data.enums.Restaurant

data class RestaurantInfo(
    val enum: Restaurant,
    val name: String,
    val location: String,
    val photoUrl: String,
    val time: String,
    val etc: String,
)