package com.eatssu.android.ui.main

import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.enums.Restaurant

data class Section(
    val menuType: MenuType,
    val cafeteria: Restaurant,
    val menuList: List<Menu>?,
//    val sortOrder: Int
)
