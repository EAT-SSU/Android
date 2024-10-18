package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.GetFixedMenuResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MenuService {
    @GET("menus") //고정 메뉴 리스트 조회
    fun getFixMenu(
        @Query("restaurant") restaurant: String,
    ): Call<BaseResponse<GetFixedMenuResponse>>

}