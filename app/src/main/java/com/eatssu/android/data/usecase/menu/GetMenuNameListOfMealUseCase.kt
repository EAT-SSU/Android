package com.eatssu.android.data.usecase.menu

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MenuOfMealResponse
import com.eatssu.android.data.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMenuNameListOfMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {
    suspend operator fun invoke(menuId: Long): Flow<BaseResponse<MenuOfMealResponse>> =
        mealRepository.getMenuInfoByMealId(menuId)
}