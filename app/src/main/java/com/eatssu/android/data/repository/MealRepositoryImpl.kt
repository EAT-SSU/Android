package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MenuOfMealResponse
import com.eatssu.android.domain.repository.MealRepository
import com.eatssu.android.data.service.MealService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(private val mealService: MealService) :
    MealRepository {

//    override suspend fun getTodayMeal(
//        date: String,
//        restaurant: String,
//        time: String
//    ): Flow<BaseResponse<ArrayList<GetMealResponse>>> =
//        flow { emit(mealService.getTodayMeal(date, restaurant, time)) }

    override suspend fun getMenuInfoByMealId(mealId: Long): Flow<BaseResponse<MenuOfMealResponse>> =
        flow {
            emit(mealService.getMenuInfoByMealId(mealId))
        }
}
