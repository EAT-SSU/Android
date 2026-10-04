package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.common.enums.Restaurant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteMenuResponse(
    @SerialName("menuId")
    val menuId: Long,
    @SerialName("menuName")
    val menuName: String,
    @SerialName("restaurant")
    val restaurant: Restaurant,
    @SerialName("isDiscontinued")
    val isDiscontinued: Boolean,
)

@Serializable
data class MenuFavoriteSearchResponse(
    @SerialName("menuId")
    val menuId: Long,
    @SerialName("menuName")
    val menuName: String,
    @SerialName("restaurant")
    val restaurant: Restaurant,
    @SerialName("isFavorite")
    val isFavorite: Boolean,
)

fun FavoriteMenuResponse.toDomain(): FavoriteMenu = FavoriteMenu(
    menuId = menuId,
    menuName = menuName,
    restaurant = restaurant,
    isDiscontinued = isDiscontinued,
)

fun MenuFavoriteSearchResponse.toDomain(): MenuFavoriteSearchResult = MenuFavoriteSearchResult(
    menuId = menuId,
    menuName = menuName,
    restaurant = restaurant,
    isFavorite = isFavorite,
)
