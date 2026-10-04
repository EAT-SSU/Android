package com.eatssu.android.domain.repository

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
     * 변동 식단 상세 화면에 표시할 전체 메뉴 이름을 가져온다.
     */
    suspend fun getMealMenuNames(mealId: Long): List<String>
}
