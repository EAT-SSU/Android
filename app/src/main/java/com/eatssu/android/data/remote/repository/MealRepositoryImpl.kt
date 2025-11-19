package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.remote.dto.response.mapTodayMenuResponseToMenu
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.MealService
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.repository.MealRepository
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealService: MealService,
) : MealRepository {

    override suspend fun getTodayMeal(
        date: String,
        restaurant: String,
        time: String
    ): List<List<String>> {
        return mealService.getTodayMeal(date, restaurant, time).orEmptyList().toDomain()
    }

    override suspend fun getTodayMenuList(
        date: String,
        restaurant: Restaurant,
        time: Time
    ): List<Menu> {
        return mealService.getTodayMeal(date, restaurant.toString(), time.toString())
            .map { it.mapTodayMenuResponseToMenu() }
            .orEmptyList()
    }

}
