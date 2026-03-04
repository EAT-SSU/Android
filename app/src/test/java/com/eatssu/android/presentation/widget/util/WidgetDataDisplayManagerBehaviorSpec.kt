package com.eatssu.android.presentation.widget.util

import com.eatssu.android.domain.model.WidgetMealInfo
import com.eatssu.android.domain.usecase.widget.GetTodayMealUseCase
import com.eatssu.android.domain.usecase.widget.MealState
import com.eatssu.android.presentation.widget.WidgetCacheManager
import com.eatssu.android.presentation.widget.WidgetMealList
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class WidgetDataDisplayManagerBehaviorSpec : AppBehaviorSpec({

    given("위젯 표시 데이터 생성") {
        val useCase = mockk<GetTodayMealUseCase>()
        val restaurant = Restaurant.HAKSIK
        val fixedClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC)

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
            WidgetCacheManager.cacheMealData(restaurant, cached, "20250101", fixedClock)

            then("useCase 호출 없이 캐시 데이터를 반환한다") {
                runTest {
                    WidgetDataDisplayManager.fetchMealInfo(
                        useCase,
                        MealTime.Morning,
                        restaurant,
                        fixedClock,
                    ) shouldBe cached
                    coVerify(exactly = 0) { useCase(any(), any()) }
                }
            }
        }

        `when`("오늘 식단 조회가 성공하면") {
            WidgetCacheManager.clearAllCache()
            coEvery { useCase("20250101", restaurant.name) } returns todaySuccess

            then("오늘 식단을 반환하고 캐시에 저장한다") {
                runTest {
                    val result = WidgetDataDisplayManager.fetchMealInfo(
                        useCase,
                        MealTime.Lunch,
                        restaurant,
                        fixedClock,
                    )
                    result shouldBe WidgetMealInfo.Available(
                        breakfast = listOf(listOf("아침A")),
                        lunch = listOf(listOf("점심A")),
                        dinner = listOf(listOf("저녁A")),
                        restaurant = restaurant,
                    )
                    WidgetCacheManager.getCachedMealData(restaurant, "20250101", fixedClock) shouldBe result
                }
            }
        }

        `when`("오늘 조회 실패 후 내일 조회가 성공하면") {
            WidgetCacheManager.clearAllCache()
            coEvery { useCase("20250101", restaurant.name) } returns MealState.Failure
            coEvery { useCase("20250102", restaurant.name) } returns nextDaySuccess

            then("내일 식단 기반 결과를 반환한다") {
                runTest {
                    val result = WidgetDataDisplayManager.fetchMealInfo(
                        useCase,
                        MealTime.Dinner,
                        restaurant,
                        fixedClock,
                    )
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
                    WidgetDataDisplayManager.fetchMealInfo(
                        useCase,
                        MealTime.Morning,
                        restaurant,
                        fixedClock,
                    ) shouldBe
                        WidgetMealInfo.Available(
                            breakfast = emptyList(),
                            lunch = emptyList(),
                            dinner = emptyList(),
                            restaurant = restaurant,
                        )
                }
            }
        }

        `when`("clock 타임존이 시스템과 달라져도") {
            WidgetCacheManager.clearAllCache()
            val losAngelesClock = Clock.fixed(
                Instant.parse("2025-01-01T00:30:00Z"),
                ZoneId.of("America/Los_Angeles"),
            )
            coEvery { useCase("20241231", restaurant.name) } returns todaySuccess

            then("clock 기준 날짜로 오늘 식단을 조회한다") {
                runTest {
                    WidgetDataDisplayManager.fetchMealInfo(
                        useCase,
                        MealTime.Morning,
                        restaurant,
                        losAngelesClock,
                    )

                    coVerify(exactly = 1) { useCase("20241231", restaurant.name) }
                    coVerify(exactly = 0) { useCase("20250101", restaurant.name) }
                }
            }
        }

        `when`("자정 전후로 연속 갱신하면") {
            WidgetCacheManager.clearAllCache()
            val beforeMidnight = Clock.fixed(Instant.parse("2025-02-01T23:59:00Z"), ZoneOffset.UTC)
            val afterMidnight = Clock.fixed(Instant.parse("2025-02-02T00:01:00Z"), ZoneOffset.UTC)

            val day1Success = MealState.Success(
                WidgetMealList(
                    breakfast = listOf(listOf("1일아침")) to "breakfast",
                    lunch = listOf(listOf("1일점심")) to "lunch",
                    dinner = listOf(listOf("1일저녁")) to "dinner",
                    restaurant = restaurant,
                )
            )
            val day2Success = MealState.Success(
                WidgetMealList(
                    breakfast = listOf(listOf("2일아침")) to "breakfast",
                    lunch = listOf(listOf("2일점심")) to "lunch",
                    dinner = listOf(listOf("2일저녁")) to "dinner",
                    restaurant = restaurant,
                )
            )

            coEvery { useCase("20250201", restaurant.name) } returns day1Success
            coEvery { useCase("20250202", restaurant.name) } returns day2Success

            then("이전 날짜 캐시를 사용하지 않고 새로운 날짜를 다시 조회한다") {
                runTest {
                    val day1Result = WidgetDataDisplayManager.fetchMealInfo(
                        useCase,
                        MealTime.Dinner,
                        restaurant,
                        beforeMidnight,
                    )

                    val day2Result = WidgetDataDisplayManager.fetchMealInfo(
                        useCase,
                        MealTime.Morning,
                        restaurant,
                        afterMidnight,
                    )

                    day1Result shouldBe WidgetMealInfo.Available(
                        breakfast = listOf(listOf("1일아침")),
                        lunch = listOf(listOf("1일점심")),
                        dinner = listOf(listOf("1일저녁")),
                        restaurant = restaurant,
                    )
                    day2Result shouldBe WidgetMealInfo.Available(
                        breakfast = listOf(listOf("2일아침")),
                        lunch = listOf(listOf("2일점심")),
                        dinner = listOf(listOf("2일저녁")),
                        restaurant = restaurant,
                    )

                    coVerify(exactly = 1) { useCase("20250201", restaurant.name) }
                    coVerify(exactly = 1) { useCase("20250202", restaurant.name) }
                }
            }
        }
    }

    given("현재 시간 기반 식사 구간 계산") {
        `when`("09시 이전이면") {
            then("Morning을 반환한다") {
                val clock = Clock.fixed(Instant.parse("2025-01-01T08:59:59Z"), ZoneOffset.UTC)
                WidgetDataDisplayManager.getCurrentMealTime(clock) shouldBe MealTime.Morning
            }
        }

        `when`("09시 이상 15시 미만이면") {
            then("Lunch를 반환한다") {
                val clock = Clock.fixed(Instant.parse("2025-01-01T14:59:59Z"), ZoneOffset.UTC)
                WidgetDataDisplayManager.getCurrentMealTime(clock) shouldBe MealTime.Lunch
            }
        }

        `when`("15시 이상이면") {
            then("Dinner를 반환한다") {
                val clock = Clock.fixed(Instant.parse("2025-01-01T15:00:00Z"), ZoneOffset.UTC)
                WidgetDataDisplayManager.getCurrentMealTime(clock) shouldBe MealTime.Dinner
            }
        }
    }
})
