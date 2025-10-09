package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.response.MenusInformation

interface MealRepository {

    /**
     * 오늘의 식단을 가져오는 api
     */
    suspend fun getTodayMeal(
        date: String,
        restaurant: String,
        time: String,
    ): List<List<String>>


    /**
     * MealId를 이용해서 Menu를 찾기 api
     */
    suspend fun getMenuInfoByMealId(
        mealId: Long,
    ): List<MenusInformation>
}