package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.model.map
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.MenuFavoriteService
import com.eatssu.android.domain.model.FavoriteMenu
import com.eatssu.android.domain.model.MenuFavoriteSearchResult
import com.eatssu.android.domain.repository.MenuFavoriteRepository
import javax.inject.Inject

class MenuFavoriteRepositoryImpl @Inject constructor(
    private val menuFavoriteService: MenuFavoriteService,
) : MenuFavoriteRepository {
    override suspend fun searchMenus(keyword: String): ApiResult<List<MenuFavoriteSearchResult>> =
        menuFavoriteService.searchMenus(keyword).map { menus -> menus.map { it.toDomain() } }

    override suspend fun getFavoriteMenus(): ApiResult<List<FavoriteMenu>> =
        menuFavoriteService.getFavoriteMenus().map { menus -> menus.map { it.toDomain() } }

    override suspend fun addFavoriteMenu(menuId: Long): ApiResult<Unit> =
        menuFavoriteService.addFavoriteMenu(menuId)

    override suspend fun removeFavoriteMenu(menuId: Long): ApiResult<Unit> =
        menuFavoriteService.removeFavoriteMenu(menuId)
}
