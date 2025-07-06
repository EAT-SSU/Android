package com.eatssu.android.domain.usecase.meal

import com.eatssu.android.data.dto.response.GetMealResponse
import com.eatssu.android.data.enums.Time
import com.eatssu.android.domain.repository.MealRepository
import com.eatssu.android.presentation.widget.we.GetMealsResponseModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

sealed interface MealException {
    /** 급식 정보가 없음 */
    data object DataEmpty : MealException

    /** 인터넷 연결 X */
    data object InternetDisconnected : MealException

    /**
     *  알 수 없는 에러 with errorCode
     *  - 사용자가 알 필요가 없는 remote error
     *  - 코드상 문제로 인한 exception
     *  */
    data class Unknown(val errorCode: String) : MealException
}

sealed interface MealState {
    data object Loading : MealState

    data class Success(val response: GetMealsResponseModel) : MealState

    data object Failure : MealState
}


class GetTodayMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {

    suspend operator fun invoke(
        date: String,
        restaurant: String
    ): MealState = runCatching {
        val breakfastFlow = mealRepository.fetchTodayMeal(date, restaurant, Time.MORNING.name)
        val lunchFlow = mealRepository.fetchTodayMeal(date, restaurant, Time.LUNCH.name)
        val dinnerFlow = mealRepository.fetchTodayMeal(date, restaurant, Time.DINNER.name)

        combine(breakfastFlow, lunchFlow, dinnerFlow) { breakfastList, lunchList, dinnerList ->
            fun mapToMealPair(
                meals: List<GetMealResponse>,
                mealType: String
            ): Pair<List<List<String>>, String> {
                // 각 meal(예: 점심)에 대해 여러 그룹(메뉴묶음)으로 변환
                val menuGroups = meals.map { meal ->
                    meal.briefMenus.mapNotNull { it.name }
                }
                return menuGroups to mealType
            }

            GetMealsResponseModel(
                breakfast = mapToMealPair(breakfastList, "breakfast"),
                lunch = mapToMealPair(lunchList, "lunch"),
                dinner = mapToMealPair(dinnerList, "dinner")
            )
        }.first() // ⬅️ 여기서 Flow 실행
    }.fold(
        onSuccess = { result ->
            Timber.d("급식 정보 가져오기 성공 $result")
            MealState.Success(result)
        },
        onFailure = { exception ->
//            val error = when (exception) {
//                is NeisException -> {
//                    when {
//                        exception.result == NeisResult.DATA_NOT_FOUND -> MealException.DataEmpty
//
//                        else -> MealException.Unknown(exception.result.code)
//                    }
//                }
//
//                is UnresolvedAddressException, is UnknownHostException -> MealException.InternetDisconnected
//
//                else -> MealException.Unknown(NeisResult.UNKNOWN_ERROR.code)
//            }

            MealState.Failure
        }
    )


}

