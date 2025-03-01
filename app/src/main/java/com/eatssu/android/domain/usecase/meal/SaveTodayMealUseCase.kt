package com.eatssu.android.domain.usecase.meal

import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.domain.repository.MealRepository
import javax.inject.Inject

class SaveTodayMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {

    suspend operator fun invoke(meal: List<GetMealResponse>) {
        return mealRepository.saveTodayMeal(meal)
    }
}
