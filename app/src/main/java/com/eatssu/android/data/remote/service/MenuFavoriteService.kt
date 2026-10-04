package com.eatssu.android.data.remote.service

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.FavoriteMenuResponse
import com.eatssu.android.data.remote.dto.response.MenuFavoriteSearchResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MenuFavoriteService {
    @GET("menu-favorites/search")
    suspend fun searchMenus(
        @Query("keyword") keyword: String,
    ): ApiResult<List<MenuFavoriteSearchResponse>>

    @GET("menu-favorites")
    suspend fun getFavoriteMenus(): ApiResult<List<FavoriteMenuResponse>>

    @POST("menu-favorites/{menuId}")
    suspend fun addFavoriteMenu(
        @Path("menuId") menuId: Long,
    ): ApiResult<Unit>

    @DELETE("menu-favorites/{menuId}")
    suspend fun removeFavoriteMenu(
        @Path("menuId") menuId: Long,
    ): ApiResult<Unit>
}
