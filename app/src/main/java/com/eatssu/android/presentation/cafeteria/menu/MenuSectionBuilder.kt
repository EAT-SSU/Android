package com.eatssu.android.presentation.cafeteria.menu

import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.model.Section
import com.eatssu.common.enums.Restaurant

internal suspend fun buildMenuSections(
    menuMap: Map<Restaurant, List<Menu>>,
    locationProvider: suspend (Restaurant) -> String,
): List<Section> = buildList {
    for ((restaurant, menuList) in menuMap) {
        add(
            Section(
                menuType = restaurant.menuType,
                cafeteria = restaurant,
                menuList = menuList,
                cafeteriaLocation = locationProvider(restaurant),
            )
        )
    }
}.sortedBy { it.cafeteria.ordinal }
