package com.eatssu.android.data.service

import com.eatssu.android.data.dto.response.ChangeMenuInfoListDto
import com.eatssu.android.data.dto.response.GetFixedMenuResponseDto
import com.eatssu.android.data.dto.response.GetTodayMealResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MenuService {
    @GET("menu/today-meal") //변동메뉴 식단 리스트 조회 By 식당
    fun getTodayMeal(
        @Query("date") date: String,
        @Query("restaurant") restaurant: String,
        @Query("time") time: String
    ): Call<GetTodayMealResponseDto>

    @GET("menu/fix-menu") //고정 메뉴 리스트 조회
    fun getFixMenu(
        @Query("restaurant") restaurant: String
    ): Call<GetFixedMenuResponseDto>

    @GET("menu/menus")
    fun getMenuByMealId(
        @Query("mealId") mealId: Long
    ): Call<ChangeMenuInfoListDto>
}