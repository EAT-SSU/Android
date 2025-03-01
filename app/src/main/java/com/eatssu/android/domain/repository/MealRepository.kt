package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.dto.response.MenuOfMealResponse
import kotlinx.coroutines.flow.Flow

interface MealRepository {

    suspend fun fetchTodayMeal2(
        date: String,
        restaurant: String,
        time: String,
    ): Flow<ArrayList<GetMealResponse>>

    suspend fun saveTodayMeal(meal: List<GetMealResponse>)

    suspend fun getMenuInfoByMealId(
        mealId: Long,
    ): Flow<BaseResponse<MenuOfMealResponse>>
}