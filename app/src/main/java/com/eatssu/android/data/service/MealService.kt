package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.ChangeMenuInfoListDto
import com.eatssu.android.data.dto.response.GetMealResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MealService {
    @GET("meals") //변동메뉴 식단 리스트 조회 By 식당
    fun getTodayMeal(
        @Query("date") date: String,
        @Query("restaurant") restaurant: String,
        @Query("time") time: String,
    ): Call<BaseResponse<ArrayList<GetMealResponse>>>

    @GET("meals/{mealId}/menus-info") //메뉴 정보 리스트 조회
    fun getMenuInfoByMealId(
        @Query("mealId") mealId: Long,
    ): Call<BaseResponse<ChangeMenuInfoListDto>>

}