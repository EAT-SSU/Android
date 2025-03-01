package com.eatssu.android.domain.usecase.meal

import com.eatssu.android.domain.repository.MealRepository
import javax.inject.Inject

class SaveTodayMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {

    suspend operator fun invoke(
        date: String,
        restaurant: String,
        time: String,
        meal: List<String>
    ) {
        return mealRepository.saveTodayMeal(date, restaurant, time, meal)
    }
}
