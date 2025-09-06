package com.eatssu.android.presentation.map.model

import com.eatssu.android.R

enum class PlaceType(val placeCategory: String, val iconRes: Int) {
    CAFE("카페", R.drawable.ic_map_cafe),
    RESTAURANT("음식점", R.drawable.ic_map_restaurant),
    PUB("주점", R.drawable.ic_map_pub),
}