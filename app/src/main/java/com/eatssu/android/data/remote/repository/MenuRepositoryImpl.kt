package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.remote.dto.response.mapFixedMenuResponseToMenu
import com.eatssu.android.data.remote.service.MenuService
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.repository.MenuRepository
import com.eatssu.common.enums.Restaurant
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(
    private val menuService: MenuService
) : MenuRepository {
    override suspend fun getFixedMenuList(restaurant: Restaurant): List<Menu> {
        return menuService.getFixMenu(restaurant.toString())
            .map { it.mapFixedMenuResponseToMenu() }
            .orEmptyList()
    }
}
