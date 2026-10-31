package com.eatssu.android.domain.model

import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time

fun isFavoriteMealAvailable(restaurant: Restaurant?, time: Time?): Boolean = when (restaurant) {
    Restaurant.HAKSIK -> time == Time.MORNING || time == Time.LUNCH
    Restaurant.DODAM, Restaurant.DORMITORY -> time == Time.LUNCH || time == Time.DINNER
    Restaurant.FACULTY -> time == Time.LUNCH
    else -> false
}
