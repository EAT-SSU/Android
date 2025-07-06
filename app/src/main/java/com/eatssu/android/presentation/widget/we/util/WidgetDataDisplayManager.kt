package com.eatssu.android.presentation.widget.we.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.domain.usecase.meal.GetTodayMealUseCase
import com.eatssu.android.domain.usecase.meal.MealState
import timber.log.Timber
import java.time.LocalTime

sealed class MealTime {

    data object Morning : MealTime()

    data object Lunch : MealTime()

    data object Dinner : MealTime()
}

sealed class MealInfoState {

    data object Loading : MealInfoState()

    data class Available(
        val mealTime: String,
        val mealList: List<List<String>>,
    ) : MealInfoState()

    data object Unavailable : MealInfoState()
}

object WidgetDataDisplayManager {

    @RequiresApi(Build.VERSION_CODES.O)
    internal suspend fun fetchMealInfo(
        getMealsUseCase: GetTodayMealUseCase,
        requestedMealTime: MealTime,
    ): MealInfoState {
        Timber.d("Widget - fetchMealInfo")
        val targetDate = DateUtils.convertMillisToDateString(System.currentTimeMillis())
        val response = getMealsUseCase(targetDate, Restaurant.DODAM.name)
        Timber.d("Widget - fetchMealInfo $response")

        if (response is MealState.Success) {
            val mealTimes = listOf(MealTime.Morning, MealTime.Lunch, MealTime.Dinner)
            val startIndex = mealTimes.indexOf(requestedMealTime)

            (startIndex..mealTimes.lastIndex).forEach { index ->
                with(response.response) {
                    val currentMealTime = mealTimes[index]
                    val currentMeal = when (currentMealTime) {
                        MealTime.Morning -> breakfast
                        MealTime.Lunch -> lunch
                        MealTime.Dinner -> dinner
                    }

                    val menuGroups = currentMeal.first
                    if (menuGroups.flatten().isNotEmpty()) {
                        return MealInfoState.Available(
                            convertTimeToString(currentMealTime),
                            menuGroups
                        )
                    }
                }
            }
        }


        val nextDay = DateUtils.getNextDayDate()
        val getNextDayMealResponse = getMealsUseCase(nextDay, Restaurant.DODAM.name)

        if (getNextDayMealResponse is MealState.Success) {
            val mealTimes = listOf(MealTime.Morning, MealTime.Lunch, MealTime.Dinner)

            (0..mealTimes.lastIndex).forEach { index ->
                with(getNextDayMealResponse.response) {
                    val currentMealTime = mealTimes[index]
                    val currentMeal = when (currentMealTime) {
                        MealTime.Morning -> breakfast
                        MealTime.Lunch -> lunch
                        MealTime.Dinner -> dinner
                    }

                    val menuGroups = currentMeal.first
                    if (menuGroups.flatten().isNotEmpty()) {
                        return MealInfoState.Available(
                            convertTimeToString(currentMealTime),
                            menuGroups
                        )
                    }
                }
            }
        }

        // 요청된 시간대부터 이후의 모든 시간대의 급식이 비어있는 경우
        return MealInfoState.Available(
            mealTime = convertTimeToString(requestedMealTime),
            mealList = emptyList()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    internal fun getCurrentMealTime(): MealTime {
        val currentTime = LocalTime.now()
        val morningEnd = LocalTime.of(9, 0)
        val lunchEnd = LocalTime.of(15, 0)

        return when {
            currentTime.isBefore(morningEnd) -> MealTime.Morning
            currentTime.isBefore(lunchEnd) -> MealTime.Lunch
            else -> MealTime.Dinner
        }
    }

    private fun convertTimeToString(time: MealTime): String {
        return when (time) {
            MealTime.Morning -> "아침"
            MealTime.Lunch -> "점심"
            MealTime.Dinner -> "저녁"
        }
    }
}