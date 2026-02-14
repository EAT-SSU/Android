package com.eatssu.android.presentation.widget.util

import com.eatssu.android.domain.model.WidgetMealInfo
import com.eatssu.android.domain.usecase.widget.GetTodayMealUseCase
import com.eatssu.android.domain.usecase.widget.MealState
import com.eatssu.android.presentation.util.CalendarUtil
import com.eatssu.android.presentation.widget.WidgetCacheManager
import com.eatssu.android.presentation.widget.WidgetMealList
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class WidgetDataDisplayManagerBehaviorSpec : AppBehaviorSpec({

    given("위젯 표시 데이터 생성") {
        val useCase = mockk<GetTodayMealUseCase>()
        val restaurant = Restaurant.HAKSIK

        mockkObject(CalendarUtil)
        every { CalendarUtil.convertMillisToDateString(any()) } returns "20250101"
        every { CalendarUtil.getNextDayDate() } returns "20250102"

        val todaySuccess = MealState.Success(
            WidgetMealList(
                breakfast = listOf(listOf("아침A")) to "breakfast",
                lunch = listOf(listOf("점심A")) to "lunch",
                dinner = listOf(listOf("저녁A")) to "dinner",
                restaurant = restaurant,
            )
        )

        val nextDaySuccess = MealState.Success(
            WidgetMealList(
                breakfast = listOf(listOf("내일아침")) to "breakfast",
                lunch = listOf(listOf("내일점심")) to "lunch",
                dinner = listOf(listOf("내일저녁")) to "dinner",
                restaurant = restaurant,
            )
        )

        `when`("캐시에 데이터가 있으면") {
            WidgetCacheManager.clearAllCache()
            val cached = WidgetMealInfo.Available(
                breakfast = listOf(listOf("cached-b")),
                lunch = listOf(listOf("cached-l")),
                dinner = listOf(listOf("cached-d")),
                restaurant = restaurant,
            )
            WidgetCacheManager.cacheMealData(restaurant, cached, "20250101")

            then("useCase 호출 없이 캐시 데이터를 반환한다") {
                runTest {
                    WidgetDataDisplayManager.fetchMealInfo(useCase, MealTime.Morning, restaurant) shouldBe cached
                    coVerify(exactly = 0) { useCase(any(), any()) }
                }
            }
        }

        `when`("오늘 식단 조회가 성공하면") {
            WidgetCacheManager.clearAllCache()
            coEvery { useCase("20250101", restaurant.name) } returns todaySuccess

            then("오늘 식단을 반환하고 캐시에 저장한다") {
                runTest {
                    val result = WidgetDataDisplayManager.fetchMealInfo(useCase, MealTime.Lunch, restaurant)
                    result shouldBe WidgetMealInfo.Available(
                        breakfast = listOf(listOf("아침A")),
                        lunch = listOf(listOf("점심A")),
                        dinner = listOf(listOf("저녁A")),
                        restaurant = restaurant,
                    )
                    WidgetCacheManager.getCachedMealData(restaurant, "20250101") shouldBe result
                }
            }
        }

        `when`("오늘 조회 실패 후 내일 조회가 성공하면") {
            WidgetCacheManager.clearAllCache()
            coEvery { useCase("20250101", restaurant.name) } returns MealState.Failure
            coEvery { useCase("20250102", restaurant.name) } returns nextDaySuccess

            then("내일 식단 기반 결과를 반환한다") {
                runTest {
                    val result = WidgetDataDisplayManager.fetchMealInfo(useCase, MealTime.Dinner, restaurant)
                    result shouldBe WidgetMealInfo.Available(
                        breakfast = listOf(listOf("내일아침")),
                        lunch = listOf(listOf("내일점심")),
                        dinner = listOf(listOf("내일저녁")),
                        restaurant = restaurant,
                    )
                    coVerify(exactly = 1) { useCase("20250101", restaurant.name) }
                    coVerify(exactly = 1) { useCase("20250102", restaurant.name) }
                }
            }
        }

        `when`("오늘/내일 모두 조회에 실패하면") {
            WidgetCacheManager.clearAllCache()
            coEvery { useCase("20250101", restaurant.name) } returns MealState.Failure
            coEvery { useCase("20250102", restaurant.name) } returns MealState.Failure

            then("빈 리스트의 Available을 반환한다") {
                runTest {
                    WidgetDataDisplayManager.fetchMealInfo(useCase, MealTime.Morning, restaurant) shouldBe
                        WidgetMealInfo.Available(
                            breakfast = emptyList(),
                            lunch = emptyList(),
                            dinner = emptyList(),
                            restaurant = restaurant,
                        )
                }
            }
        }
    }
})
