package com.eatssu.android.presentation.favorite

import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.model.isFavoriteMealAvailable
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import java.time.LocalDate

internal val todayMenuRestaurants = listOf(
    Restaurant.HAKSIK, Restaurant.DODAM, Restaurant.DORMITORY, Restaurant.FACULTY,
)

internal fun isTodayMealAvailable(restaurant: Restaurant?, time: Time?): Boolean =
    isFavoriteMealAvailable(restaurant, time)

data class TodayMenuFavoriteState(
    val expanded: Boolean = false,
    val restaurant: Restaurant = Restaurant.HAKSIK,
    val time: Time = Time.LUNCH,
    val date: LocalDate = LocalDate.now(),
    val menus: List<MenuFavoriteSearchResult> = emptyList(),
    val selectedMenuIds: Set<Long> = emptySet(),
    val loadState: MenuFavoriteLoadState = MenuFavoriteLoadState.Idle,
    val isSaving: Boolean = false,
)
