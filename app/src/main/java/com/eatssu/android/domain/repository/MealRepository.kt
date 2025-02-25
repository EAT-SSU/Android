package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.dto.response.MenuOfMealResponse
import kotlinx.coroutines.flow.Flow

interface MealRepository {

    suspend fun fetchTodayMeal(
        date: String,
        restaurant: String,
        time: String,
    ): Result<BaseResponse<ArrayList<GetMealResponse>>>

    fun getTodayMealFlow(): Flow<ArrayList<GetMealResponse>>

//    suspend fun getTodayMeal2(
//        date: String,
//        restaurant: String,
//        time: String,
//    ): Flow<BaseResponse<ArrayList<GetMealResponse>>>

    suspend fun getMenuInfoByMealId(
        mealId: Long,
    ): Flow<BaseResponse<MenuOfMealResponse>>
}