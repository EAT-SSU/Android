package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.ModifyMenuRequest
import com.eatssu.android.data.model.response.GetMenuInfoListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MenuService {
    @GET("menu/{date}/morning") //특정 식당의 아침메뉴 조회
    fun getMorningMenu(
        @Path("date") data: String, @Path("restaurant") restaurant: String
    ): Call<GetMenuInfoListResponse>

    @POST("menu/{date}/morning") //특정 식당의 아침메뉴 추가
    fun modifyMorningMenu(
        @Path("date") data: String,
        @Path("restaurant") restaurant: String,
        @Body request: ModifyMenuRequest
    ): Call<String>

    @GET("menu/{date}/lunch") //특정 식당의 점심메뉴 조회
    fun getLunchMenu(
        @Path("date") data: String, @Path("restaurant") restaurant: String
    ): Call<GetMenuInfoListResponse>

    @POST("menu/{date}/lunch") //특정 식당의 점심메뉴 추가
    fun modifyLunchMenu(
        @Path("date") data: String,
        @Path("restaurant") restaurant: String,
        @Body request: ModifyMenuRequest
    ): Call<String>

    @GET("menu/{date}/dinner") //특정 식당의 저녁메뉴 조회
    fun getDinnerMenu(
        @Path("date") data: String, @Path("restaurant") restaurant: String
    ): Call<GetMenuInfoListResponse>

    @POST("menu/{date}/dinner") //특정 식당의 저녁메뉴 추가
    fun modifyDinnerMenu(
        @Path("date") data: String,
        @Path("restaurant") restaurant: String,
        @Body request: ModifyMenuRequest
    ): Call<String>

    @GET("menu") //고정메뉴 파는거 조회
    fun getFixedMenu(@Query("restaurant") restaurant: String): Call<GetMenuInfoListResponse>
}