package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.ChangeMenuInfoListDto
import com.eatssu.android.data.dto.response.FixMenuInfoList
import com.eatssu.android.data.dto.response.GetTodayMealResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MenuService {
    @GET("meals") //변동메뉴 식단 리스트 조회 By 식당
    fun getTodayMeal(
        @Query("date") date: String,
        @Query("restaurant") restaurant: String,
        @Query("time") time: String
    ): Call<BaseResponse<GetTodayMealResponseDto>>

    @GET("menus") //고정 메뉴 리스트 조회
    fun getFixMenu(
        @Query("restaurant") restaurant: String
    ): Call<BaseResponse<ArrayList<FixMenuInfoList>>>

    @GET("menus/in-meal")
    fun getMenuInfoByMealId(
        @Query("mealId") mealId: Long
    ): Call<BaseResponse<ChangeMenuInfoListDto>>
}