package com.eatssu.android.domain.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult

interface MenuFavoriteRepository {
    suspend fun searchMenus(keyword: String): ApiResult<List<MenuFavoriteSearchResult>>
    suspend fun getFavoriteMenus(): ApiResult<List<FavoriteMenu>>
    suspend fun addFavoriteMenu(menuId: Long): ApiResult<Unit>
    suspend fun removeFavoriteMenu(menuId: Long): ApiResult<Unit>
}
