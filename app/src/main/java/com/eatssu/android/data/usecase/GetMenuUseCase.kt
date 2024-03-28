package com.eatssu.android.data.usecase

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MenuResponse
import com.eatssu.android.data.repository.MenuRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMenuUseCase @Inject constructor(
    private val menuRepository: MenuRepository,
) {
    suspend operator fun invoke(restaurant: String): Flow<BaseResponse<MenuResponse>> =
        menuRepository.getMenu(restaurant)
}