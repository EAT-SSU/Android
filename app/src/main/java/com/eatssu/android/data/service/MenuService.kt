package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MenuResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MenuService {
    @GET("menus") //고정 메뉴 리스트 조회
    suspend fun getMenu(
        @Query("restaurant") restaurant: String,
    ): BaseResponse<MenuResponse>

}