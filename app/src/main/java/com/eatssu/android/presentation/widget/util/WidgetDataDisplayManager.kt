package com.eatssu.android.presentation.widget.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.domain.model.WidgetMealInfo
import com.eatssu.android.domain.usecase.meal.GetTodayMealUseCase
import com.eatssu.android.domain.usecase.meal.MealState
import com.eatssu.android.presentation.util.CalendarUtil
import com.eatssu.android.presentation.widget.WidgetCacheManager
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
        val restaurant: Restaurant,
    ) : MealInfoState()

    data object Unavailable : MealInfoState()
}

object WidgetDataDisplayManager {

    @RequiresApi(Build.VERSION_CODES.O)
    internal suspend fun fetchMealInfo(
        getMealsUseCase: GetTodayMealUseCase,
        requestedMealTime: MealTime,
        restaurant: Restaurant,
    ): MealInfoState {
        Timber.d("Widget - fetchMealInfo")
        val targetDate = CalendarUtil.convertMillisToDateString(System.currentTimeMillis())

        // 캐시에서 데이터 확인
        val cachedMealInfo = WidgetCacheManager.getCachedMealData(restaurant, targetDate)
        if (cachedMealInfo != null) {
            return when (cachedMealInfo) {
                is WidgetMealInfo.Available -> MealInfoState.Available(
                    cachedMealInfo.mealTime,
                    cachedMealInfo.mealList,
                    cachedMealInfo.restaurant
                )

                WidgetMealInfo.Loading -> MealInfoState.Loading
                WidgetMealInfo.Unavailable -> MealInfoState.Unavailable
            }
        }
        
        val response = getMealsUseCase(targetDate, restaurant.name)
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
                        val mealInfo = MealInfoState.Available(
                            convertTimeToString(currentMealTime),
                            menuGroups,
                            restaurant
                        )

                        // 캐시에 저장
                        WidgetCacheManager.cacheMealData(
                            restaurant,
                            WidgetMealInfo.Available(
                                mealInfo.mealTime,
                                mealInfo.mealList,
                                mealInfo.restaurant
                            ),
                            targetDate
                        )

                        return mealInfo
                    }
                }
            }
        }

        val nextDay = CalendarUtil.getNextDayDate()
        val getNextDayMealResponse = getMealsUseCase(nextDay, restaurant.name)

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
                        val mealInfo = MealInfoState.Available(
                            convertTimeToString(currentMealTime),
                            menuGroups,
                            restaurant
                        )

                        // 캐시에 저장
                        WidgetCacheManager.cacheMealData(
                            restaurant,
                            WidgetMealInfo.Available(
                                mealInfo.mealTime,
                                mealInfo.mealList,
                                mealInfo.restaurant
                            ),
                            targetDate
                        )

                        return mealInfo
                    }
                }
            }
        }

        // 요청된 시간대부터 이후의 모든 시간대의 급식이 비어있는 경우
        val emptyMealInfo = MealInfoState.Available(
            mealTime = convertTimeToString(requestedMealTime),
            mealList = emptyList(),
            restaurant = restaurant
        )

        // 캐시에 저장
        WidgetCacheManager.cacheMealData(
            restaurant,
            WidgetMealInfo.Available(
                emptyMealInfo.mealTime,
                emptyMealInfo.mealList,
                emptyMealInfo.restaurant
            ),
            targetDate
        )

        return emptyMealInfo
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