package com.eatssu.android.data.remote.service

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.GetMealMenusInfoResponse
import com.eatssu.android.data.remote.dto.response.GetMealResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MealService {
    /**
     * 변동메뉴 식단 리스트 조회 By 식당
     */
    @GET("meals")
    suspend fun getTodayMeal(
        @Query("date") date: String,
        @Query("restaurant") restaurant: String,
        @Query("time") time: String,
        @Query("language") language: String? = null,
    ): ApiResult<List<GetMealResponse>>

    @GET("meals/{mealId}/menus-info")
    suspend fun getMealMenusInfo(
        @Path("mealId") mealId: Long,
        @Query("language") language: String? = null,
    ): ApiResult<GetMealMenusInfoResponse>
}
