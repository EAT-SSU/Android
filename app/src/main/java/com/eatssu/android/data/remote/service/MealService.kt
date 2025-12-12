package com.eatssu.android.data.remote.service

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.GetMealResponse
import retrofit2.http.GET
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
    ): ApiResult<List<GetMealResponse>>

}
