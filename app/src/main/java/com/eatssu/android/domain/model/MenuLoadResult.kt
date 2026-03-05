package com.eatssu.android.domain.model

import com.eatssu.common.enums.Restaurant

data class MenuLoadResult(
    val menuMap: Map<Restaurant, List<Menu>>,
    val publicHolidayName: String?,
)
