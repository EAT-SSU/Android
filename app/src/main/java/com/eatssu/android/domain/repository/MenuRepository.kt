package com.eatssu.android.domain.repository

import com.eatssu.android.domain.model.Menu
import com.eatssu.common.enums.Restaurant

interface MenuRepository {
    /**
     * 고정 메뉴 리스트 조회
     */
    suspend fun getFixedMenuList(restaurant: Restaurant): List<Menu>
}
