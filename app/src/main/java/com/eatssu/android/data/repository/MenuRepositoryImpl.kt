package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MenuResponse
import com.eatssu.android.data.service.MenuService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(private val menuService: MenuService) :
    MenuRepository {

    override suspend fun getMenu(restaurant: String): Flow<BaseResponse<MenuResponse>> =
        flow {
            emit(menuService.getMenu(restaurant))
        }

}
