package com.eatssu.android.domain.usecase.menu

import com.eatssu.android.domain.repository.MealRepository
import javax.inject.Inject

class GetMealMenuNamesUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {
    suspend operator fun invoke(mealId: Long): List<String> =
        mealRepository.getMealMenuNames(mealId)
}
