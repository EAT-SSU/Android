package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.response.MenusInformation
import com.eatssu.android.data.dto.response.toDomain
import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.service.MealService
import com.eatssu.android.domain.repository.MealRepository
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

    override suspend fun getMenuInfoByMealId(mealId: Long): List<MenusInformation> =
        mealService.getMenuInfoByMealId(mealId).map { it.briefMenus }.orEmptyList()
}
