package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.model.map
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.MealService
import com.eatssu.android.data.remote.service.MenuFavoriteService
import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.repository.MenuFavoriteRepository
import com.eatssu.common.enums.AppLanguage
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MenuFavoriteRepositoryImpl @Inject constructor(
    private val menuFavoriteService: MenuFavoriteService,
    private val mealService: MealService,
    private val settingDataStore: SettingDataStore,
) : MenuFavoriteRepository {
    override suspend fun getTodayMenus(
        date: LocalDate,
        restaurant: Restaurant,
        time: Time,
    ): ApiResult<List<MenuFavoriteSearchResult>> {
        val language =
            if (settingDataStore.appLanguage.first() == AppLanguage.KOREAN) null else "EN"
        return mealService.getTodayMeal(
            date = date.format(DateTimeFormatter.BASIC_ISO_DATE),
            restaurant = restaurant.name,
            time = time.name,
            language = language,
        ).map { meals ->
            meals.flatMap { it.briefMenus }.mapNotNull { menu ->
                val id = menu.menuId?.takeIf { it > 0 } ?: return@mapNotNull null
                val name = menu.name?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                MenuFavoriteSearchResult(id, name, restaurant, isFavorite = false)
            }.distinctBy { it.menuId }
        }
    }

    override suspend fun searchMenus(keyword: String): ApiResult<List<MenuFavoriteSearchResult>> =
        menuFavoriteService.searchMenus(keyword).map { menus -> menus.map { it.toDomain() } }

    override suspend fun getFavoriteMenus(): ApiResult<List<FavoriteMenu>> =
        menuFavoriteService.getFavoriteMenus().map { menus -> menus.map { it.toDomain() } }

    override suspend fun addFavoriteMenu(menuId: Long): ApiResult<Unit> =
        menuFavoriteService.addFavoriteMenu(menuId)

    override suspend fun removeFavoriteMenu(menuId: Long): ApiResult<Unit> =
        menuFavoriteService.removeFavoriteMenu(menuId)
}
