package com.eatssu.android.domain.model

import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant

data class Section(
    val menuType: MenuType,
    val cafeteria: Restaurant,
    val menuList: List<Menu>?,
    val cafeteriaLocation: String
//    val sortOrder: Int
)
