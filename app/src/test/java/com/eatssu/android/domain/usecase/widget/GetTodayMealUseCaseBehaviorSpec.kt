package com.eatssu.android.domain.usecase.widget

import com.eatssu.android.domain.repository.MealRepository
import com.eatssu.android.presentation.widget.WidgetMealList
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

@OptIn(ExperimentalCoroutinesApi::class)
class GetTodayMealUseCaseBehaviorSpec : AppBehaviorSpec({

    given("위젯 오늘의 식단 조회") {
        val mealRepository = mockk<MealRepository>()
        val useCase = GetTodayMealUseCase(mealRepository)

        `when`("아침/점심/저녁 조회가 모두 성공하면") {
            val breakfast = listOf(listOf("아침A"))
            val lunch = listOf(listOf("점심A", "점심B"))
            val dinner = listOf(listOf("저녁A"))

            coEvery { mealRepository.getTodayMeal("2025-01-01", "HAKSIK", Time.MORNING.name) } returns breakfast
            coEvery { mealRepository.getTodayMeal("2025-01-01", "HAKSIK", Time.LUNCH.name) } returns lunch
            coEvery { mealRepository.getTodayMeal("2025-01-01", "HAKSIK", Time.DINNER.name) } returns dinner

            then("MealState.Success와 WidgetMealList를 반환한다") {
                runTest {
                    useCase("2025-01-01", "HAKSIK") shouldBe MealState.Success(
                        WidgetMealList(
                            breakfast = breakfast to "breakfast",
                            lunch = lunch to "lunch",
                            dinner = dinner to "dinner",
                            restaurant = Restaurant.HAKSIK,
                        )
                    )
                }
            }
        }

        `when`("네트워크 주소 해석 예외가 발생하면") {
            coEvery {
                mealRepository.getTodayMeal("2025-01-01", "HAKSIK", Time.MORNING.name)
            } throws UnknownHostException("offline")

            then("MealState.Failure를 반환한다") {
                runTest {
                    useCase("2025-01-01", "HAKSIK") shouldBe MealState.Failure
                }
            }
        }

        `when`("네트워크 주소 미해결 예외가 발생하면") {
            coEvery {
                mealRepository.getTodayMeal("2025-01-01", "HAKSIK", Time.MORNING.name)
            } throws UnresolvedAddressException()

            then("MealState.Failure를 반환한다") {
                runTest {
                    useCase("2025-01-01", "HAKSIK") shouldBe MealState.Failure
                }
            }
        }

        `when`("알 수 없는 예외가 발생하면") {
            coEvery {
                mealRepository.getTodayMeal("2025-01-01", "HAKSIK", Time.MORNING.name)
            } throws IllegalStateException("boom")

            then("MealState.Failure를 반환한다") {
                runTest {
                    useCase("2025-01-01", "HAKSIK") shouldBe MealState.Failure
                }
            }
        }
    }
})
