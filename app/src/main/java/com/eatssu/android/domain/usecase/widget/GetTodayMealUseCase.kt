package com.eatssu.android.domain.usecase.widget

import com.eatssu.android.domain.repository.MealRepository
import com.eatssu.android.presentation.widget.WidgetMealList
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import timber.log.Timber
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
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

    data class Success(val response: WidgetMealList) : MealState

    data object Failure : MealState
}


class GetTodayMealUseCase @Inject constructor(
    private val mealRepository: MealRepository,
) {
    suspend operator fun invoke(
        date: String,
        restaurant: String
    ): MealState = runCatching {
        val breakfastList = mealRepository.getTodayMeal(date, restaurant, Time.MORNING.name)
        val lunchList = mealRepository.getTodayMeal(date, restaurant, Time.LUNCH.name)
        val dinnerList = mealRepository.getTodayMeal(date, restaurant, Time.DINNER.name)

        WidgetMealList(
            breakfast = (breakfastList to "breakfast"),
            lunch = (lunchList to "lunch"),
            dinner = (dinnerList to "dinner"),
            restaurant = Restaurant.valueOf(restaurant)
        )
    }.fold(
        onSuccess = { result ->
            Timber.d("메뉴 가져오기 성공 $result")
            MealState.Success(result)
        },
        onFailure = { exception ->
            val error = when (exception) {

                is UnresolvedAddressException, is UnknownHostException -> MealException.InternetDisconnected

                else -> MealException.Unknown("알 수 없는 에러")
            }

            MealState.Failure
        }
    )
}
