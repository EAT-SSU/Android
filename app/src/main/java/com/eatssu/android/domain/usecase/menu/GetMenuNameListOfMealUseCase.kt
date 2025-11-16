package com.eatssu.android.domain.usecase.menu

import com.eatssu.android.data.remote.dto.response.MenusInformation
import com.eatssu.android.domain.repository.MealRepository
import javax.inject.Inject

class GetMenuNameListOfMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {
    suspend operator fun invoke(menuId: Long): List<MenusInformation> =
        mealRepository.getMenuInfoByMealId(menuId)
}