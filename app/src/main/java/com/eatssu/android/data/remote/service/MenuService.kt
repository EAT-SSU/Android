package com.eatssu.android.data.remote.service

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.GetFixedMenuResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MenuService {

    /**
     * 고정 메뉴 리스트 조회
     */
    @GET("menus")
    suspend fun getFixMenu(
        @Query("restaurant") restaurant: String,
        @Query("language") language: String? = null,
    ): ApiResult<GetFixedMenuResponse>

}
