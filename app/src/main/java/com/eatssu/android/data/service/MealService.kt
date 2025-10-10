package com.eatssu.android.data.service

import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.dto.response.MenuOfMealResponse
import com.eatssu.android.data.model.ApiResult
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MealService {
    @GET("meals")
    suspend fun getTodayMeal(
        @Query("date") date: String,
        @Query("restaurant") restaurant: String,
        @Query("time") time: String,
    ): ApiResult<List<GetMealResponse>>

    @GET("meals/{mealId}/menus-info")
    suspend fun getMenuInfoByMealId(
        @Path("mealId") mealId: Long,
    ): ApiResult<MenuOfMealResponse>

}