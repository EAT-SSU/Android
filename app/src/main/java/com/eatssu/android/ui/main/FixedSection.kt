package com.eatssu.android.ui.main

import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto

data class FixedSection(
    val cafeteria: Restaurant,
    val menuList: GetFixedMenuResponseDto
):MenuSection
