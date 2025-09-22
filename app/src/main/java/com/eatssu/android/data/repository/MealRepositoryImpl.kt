package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.response.toDomain
import com.eatssu.android.data.service.MealService
import com.eatssu.android.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealService: MealService,
) : MealRepository {

    override suspend fun getTodayMeal( //todo 분기처리 어떻게 할지?
        date: String,
        restaurant: String,
        time: String
    ): Flow<List<List<String>>> {
        return flow {
            try {
                val response = mealService.getTodayMeal2(date, restaurant, time)

                // 응답이 성공적이라면 Result.success()로 감싸서 Flow로 반환
                if (response.isSuccess == true) {
                    response.result?.let { emit(it.toDomain()) } // 성공시 데이터를 반환
                } else {
                    // 실패한 경우에는 Result.failure()로 실패 정보 반환
                    emit(emptyList())
                }
            } catch (e: Exception) {
                // 네트워크 오류 또는 예외가 발생한 경우에는 Result.failure()로 반환
//                emit(ApiResult.Failure(e))
            }
        }
    }
}

