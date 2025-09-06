package com.eatssu.android.data.service

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.dto.response.MenuOfMealResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MealService {
    /**
     * 변동메뉴 식단 리스트 조회 By 식당
     */
    @GET("meals")
    fun getTodayMeal(
        @Query("date") date: String,
        @Query("restaurant") restaurant: String,
        @Query("time") time: String,
    ): Call<BaseResponse<ArrayList<GetMealResponse>>>

    // todo 위에 함수를 call 없애서 하나로 합치길 바람 ㅜㅜ 위젯 때문에 급하게 복사본을 만듦
    @GET("meals")
    suspend fun getTodayMeal2(
        @Query("date") date: String,
        @Query("restaurant") restaurant: String,
        @Query("time") time: String,
    ): BaseResponse<ArrayList<GetMealResponse>>

    /**
     * 메뉴 정보 리스트 조회
     */
    @GET("meals/{mealId}/menus-info")
    suspend fun getMenuInfoByMealId(
        @Path("mealId") mealId: Long,
    ): BaseResponse<MenuOfMealResponse>

}