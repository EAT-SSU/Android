package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.response.MenusInformation
import com.eatssu.android.domain.model.Menu
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time

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
     * 오늘의 식단을 Menu 리스트로 가져오는 api
     */
    suspend fun getTodayMenuList(
        date: String,
        restaurant: Restaurant,
        time: Time,
    ): List<Menu>

    /**
     * MealId를 이용해서 Menu를 찾기 api
     */
    suspend fun getMenuInfoByMealId(
        mealId: Long,
    ): List<MenusInformation>
}