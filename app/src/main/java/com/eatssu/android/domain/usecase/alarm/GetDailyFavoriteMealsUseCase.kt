package com.eatssu.android.domain.usecase.alarm

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.model.isFavoriteMealAvailable
import com.eatssu.android.domain.repository.MenuFavoriteRepository
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import javax.inject.Inject

data class FavoriteMealMatch(
    val date: LocalDate,
    val time: Time,
    val menus: List<MenuFavoriteSearchResult>,
) {
    // Same order as buildMenuSections(): the first restaurant visible in the cafeteria list.
    val targetRestaurant: Restaurant? get() = menus.minByOrNull { it.restaurant.ordinal }?.restaurant
}

class GetDailyFavoriteMealsUseCase @Inject constructor(private val repository: MenuFavoriteRepository) {
    suspend operator fun invoke(date: LocalDate): List<FavoriteMealMatch> = coroutineScope {
        val favorites = (repository.getFavoriteMenus() as? ApiResult.Success)?.data.orEmpty()
        if (favorites.isEmpty()) return@coroutineScope emptyList()
        val restaurants = Restaurant.getVariableRestaurantList().filter { restaurant ->
            favorites.any { it.restaurant == restaurant }
        }
        val requests = listOf(Time.LUNCH, Time.DINNER).flatMap { time ->
            restaurants.filter { isFavoriteMealAvailable(it, time) }.map { restaurant ->
                async {
                    val ids =
                        favorites.filter { it.restaurant == restaurant }.map { it.menuId }.toSet()
                    val menus = (repository.getTodayMenus(
                        date,
                        restaurant,
                        time
                    ) as? ApiResult.Success)?.data.orEmpty()
                    time to menus.filter { it.restaurant == restaurant && it.menuId in ids }
                }
            }
        }.awaitAll()
        listOf(Time.LUNCH, Time.DINNER).mapNotNull { time ->
            val matches = requests.filter { it.first == time }.flatMap { it.second }
                .distinctBy { it.restaurant to it.menuId }
                .sortedBy { it.restaurant.ordinal }
            matches.takeIf { it.isNotEmpty() }?.let { FavoriteMealMatch(date, time, it) }
        }
    }
}
