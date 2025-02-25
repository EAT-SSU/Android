package com.eatssu.android.data.repository

import com.eatssu.android.data.datastore.MealDataStore
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.dto.response.MenuOfMealResponse
import com.eatssu.android.data.service.MealService
import com.eatssu.android.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealService: MealService,
    private val mealDataStore: MealDataStore
) :
    MealRepository {

//    override suspend fun getTodayMeal(
//        date: String,
//        restaurant: String,
//        time: String
//    ): Flow<BaseResponse<ArrayList<GetMealResponse>>> =
//        flow { emit(mealService.getTodayMeal(date, restaurant, time)) }


    override suspend fun fetchTodayMeal(
        date: String,
        restaurant: String,
        time: String
    ): Result<BaseResponse<ArrayList<GetMealResponse>>> {
        return try {
            // API 호출
            val response = mealService.getTodayMeal2(date, restaurant, time)

            if (response.isSuccess == true) {
                val mealList = response.result ?: arrayListOf() // null이면 빈 리스트 반환
                mealDataStore.saveMeal(mealList) // DataStore에 저장
                Result.success(response) // BaseResponse 반환
            } else {
                Result.failure(Exception(response.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override fun getTodayMealFlow(): Flow<ArrayList<GetMealResponse>> {
        return mealDataStore.getMealFlow() // DataStore에서 Flow로 제공
        //
    }

//    override suspend fun getTodayMeal2(
//        date: String,
//        restaurant: String,
//        time: String
//    ): Flow<BaseResponse<ArrayList<GetMealResponse>>> =
//        flow { emit(mealService.getTodayMeal2(date, restaurant, time)) }

    override suspend fun getMenuInfoByMealId(mealId: Long): Flow<BaseResponse<MenuOfMealResponse>> =
        flow {
            emit(mealService.getMenuInfoByMealId(mealId))
        }
}
