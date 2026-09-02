package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.local.SettingDataStore
import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.remote.dto.response.mapFixedMenuResponseToMenu
import com.eatssu.android.data.remote.service.MenuService
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.repository.MenuRepository
import com.eatssu.common.enums.AppLanguage
import com.eatssu.common.enums.Restaurant
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private const val ENGLISH_FIXED_MENU_LANGUAGE = "EN"

class MenuRepositoryImpl @Inject constructor(
    private val menuService: MenuService,
    private val settingDataStore: SettingDataStore,
) : MenuRepository {
    override suspend fun getFixedMenuList(restaurant: Restaurant): List<Menu> {
        val language = when (settingDataStore.appLanguage.first()) {
            AppLanguage.KOREAN -> null
            AppLanguage.ENGLISH,
            AppLanguage.JAPANESE,
            AppLanguage.VIETNAMESE -> ENGLISH_FIXED_MENU_LANGUAGE
        }
        return menuService.getFixMenu(restaurant.toString(), language)
            .map { it.mapFixedMenuResponseToMenu() }
            .orEmptyList()
    }
}
