package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.dto.response.MenuOfMealResponse
import kotlinx.coroutines.flow.Flow

interface MealRepository {

    // 오늘의 식단을 가져오는 api
    suspend fun fetchTodayMeal(
        date: String,
        restaurant: String,
        time: String,
    ): Flow<ArrayList<GetMealResponse>>


    // MealId를 이용해서 Menu를 찾기 api
    suspend fun getMenuInfoByMealId(
        mealId: Long,
    ): Flow<BaseResponse<MenuOfMealResponse>>
}