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

//    override suspend fun fetchTodayMeal(
//        date: String,
//        restaurant: String,
//        time: String
//    ): Result<BaseResponse<ArrayList<GetMealResponse>>> {
//        return try {
//            // API 호출
//            val response = mealService.getTodayMeal2(date, restaurant, time)
//
//            if (response.isSuccess == true) {
//                val mealList = response.result ?: arrayListOf() // null이면 빈 리스트 반환
//                mealDataStore.saveMeal(mealList) // DataStore에 저장
//                Result.success(response) // BaseResponse 반환
//            } else {
//                Result.failure(Exception(response.message ?: "Unknown error"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    override suspend fun fetchTodayMeal2(
        date: String,
        restaurant: String,
        time: String
    ): Flow<ArrayList<GetMealResponse>> {
        return flow {
            try {
                // API 호출 예시
                val response = mealService.getTodayMeal2(date, restaurant, time)

                // 응답이 성공적이라면 Result.success()로 감싸서 Flow로 반환
                if (response.isSuccess == true) {
                    response.result?.let { emit(it) } // 성공시 데이터를 반환
                } else {
                    // 실패한 경우에는 Result.failure()로 실패 정보 반환
//                    emit(response.message))
                }
            } catch (e: Exception) {
                // 네트워크 오류 또는 예외가 발생한 경우에는 Result.failure()로 반환
//                emit(ApiResult.Failure(e))
            }
        }
    }

    override suspend fun saveTodayMeal(
        date: String,
        restaurant: String,
        time: String,
        meal: List<String>
    ) {
        mealDataStore.saveMeal(date, restaurant, time, meal)
    }


    override suspend fun getMenuInfoByMealId(mealId: Long): Flow<BaseResponse<MenuOfMealResponse>> =
        flow {
            emit(mealService.getMenuInfoByMealId(mealId))
        }
}
