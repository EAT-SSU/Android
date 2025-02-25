package com.eatssu.android.domain.usecase.meal

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {
    suspend fun execute(
        date: String,
        restaurant: String,
        time: String
    ): Result<BaseResponse<ArrayList<GetMealResponse>>> =
        mealRepository.fetchTodayMeal(date, restaurant, time)

    fun observeWeather(): Flow<ArrayList<GetMealResponse>> = mealRepository.getTodayMealFlow()


}

