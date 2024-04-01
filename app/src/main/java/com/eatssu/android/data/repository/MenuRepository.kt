package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MenuResponse
import kotlinx.coroutines.flow.Flow

interface MenuRepository {

    suspend fun getMenu(
        restaurant: String,
    ): Flow<BaseResponse<MenuResponse>>

}

