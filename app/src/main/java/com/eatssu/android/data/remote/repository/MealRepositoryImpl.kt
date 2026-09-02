package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.model.orNull
import com.eatssu.android.data.remote.dto.response.mapTodayMenuResponseToMenu
import com.eatssu.android.data.remote.dto.response.toMenuNames
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.MealService
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.repository.MealRepository
import com.eatssu.common.enums.AppLanguage
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val ENGLISH_MEAL_LANGUAGE = "EN"

class MealRepositoryImpl @Inject constructor(
    private val mealService: MealService,
    private val settingDataStore: SettingDataStore,
) : MealRepository {

    override suspend fun getTodayMeal(
        date: String,
        restaurant: String,
        time: String
    ): List<List<String>> {
        val language = getMealLanguage()
        return mealService.getTodayMeal(date, restaurant, time, language)
            .orEmptyList()
            .toDomain(showMainMenusOnly = language != null)
    }

    override suspend fun getTodayMenuList(
        date: String,
        restaurant: Restaurant,
        time: Time
    ): List<Menu> {
        val language = getMealLanguage()
        return mealService.getTodayMeal(date, restaurant.toString(), time.toString(), language)
            .map { it.mapTodayMenuResponseToMenu(showMainMenusOnly = language != null) }
            .orEmptyList()
    }

    override suspend fun getMealMenuNames(mealId: Long): List<String> {
        return mealService.getMealMenusInfo(mealId, getMealLanguage())
            .orNull()
            ?.toMenuNames()
            .orEmpty()
    }

    private suspend fun getMealLanguage(): String? {
        return when (settingDataStore.appLanguage.first()) {
            AppLanguage.KOREAN -> null
            AppLanguage.ENGLISH,
            AppLanguage.JAPANESE,
            AppLanguage.VIETNAMESE -> ENGLISH_MEAL_LANGUAGE
        }
    }
}
