package com.eatssu.android.domain.repository

import kotlinx.coroutines.flow.Flow

interface MealRepository {

    /**
     * 오늘의 식단을 가져오는 api
     */
    suspend fun getTodayMeal(
        date: String,
        restaurant: String,
        time: String,
    ): Flow<List<List<String>>>

}
