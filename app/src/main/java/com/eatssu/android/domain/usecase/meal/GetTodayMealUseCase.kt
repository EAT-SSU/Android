package com.eatssu.android.domain.usecase.meal

import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTodayMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
    private val saveTodayMealUseCase: SaveTodayMealUseCase
) {

    suspend fun fetchMealData(
        date: String,
        restaurant: String,
        time: String
    ):
            Flow<ArrayList<GetMealResponse>> {
        val data = mealRepository.fetchTodayMeal2(date, restaurant, time)

// Flow를 List로 변환 후 저장
        val mealList: List<GetMealResponse> = data.first()
        saveTodayMealUseCase(mealList)
        return data

    }


}

