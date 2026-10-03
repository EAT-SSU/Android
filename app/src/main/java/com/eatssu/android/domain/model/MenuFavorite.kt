package com.eatssu.android.domain.model

import com.eatssu.common.enums.Restaurant

data class FavoriteMenu(
    val menuId: Long,
    val menuName: String,
    val restaurant: Restaurant,
    val isDiscontinued: Boolean,
)

data class MenuFavoriteSearchResult(
    val menuId: Long,
    val menuName: String,
    val restaurant: Restaurant,
    val isFavorite: Boolean,
)
