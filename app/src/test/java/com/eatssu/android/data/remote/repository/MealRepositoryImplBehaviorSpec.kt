package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.GetMealResponse
import com.eatssu.android.data.remote.dto.response.MenusInformationList
import com.eatssu.android.data.remote.service.MealService
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class MealRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("MealRepositoryImpl") {
        val mealService = mockk<MealService>()
        val repository = MealRepositoryImpl(mealService)

        val mealResponse = listOf(
            GetMealResponse(
                mealId = 10L,
                price = 5000,
                rating = 4.0,
                briefMenus = listOf(
                    MenusInformationList(menuId = 1L, name = "제육"),
                    MenusInformationList(menuId = 2L, name = "계란찜"),
                ),
            )
        )

        `when`("getTodayMeal API가 성공하면") {
            coEvery { mealService.getTodayMeal("2025-01-01", "HAKSIK", "LUNCH") } returns ApiResult.Success(mealResponse)

            then("메뉴 이름 리스트 리스트로 변환한다") {
                runTest {
                    repository.getTodayMeal("2025-01-01", "HAKSIK", "LUNCH") shouldBe listOf(
                        listOf("제육", "계란찜")
                    )
                }
            }
        }

        `when`("getTodayMeal API가 실패하면") {
            coEvery { mealService.getTodayMeal("2025-01-01", "HAKSIK", "LUNCH") } returns ApiResult.Failure(500, "err")

            then("빈 리스트를 반환한다") {
                runTest {
                    repository.getTodayMeal("2025-01-01", "HAKSIK", "LUNCH") shouldBe emptyList()
                }
            }
        }

        `when`("getTodayMenuList API가 성공하면") {
            coEvery {
                mealService.getTodayMeal("2025-01-01", Restaurant.HAKSIK.toString(), Time.LUNCH.toString())
            } returns ApiResult.Success(mealResponse)

            then("Menu 도메인 리스트로 변환한다") {
                runTest {
                    val result = repository.getTodayMenuList("2025-01-01", Restaurant.HAKSIK, Time.LUNCH)
                    result.size shouldBe 1
                    result.first().name shouldBe "제육, 계란찜"
                }
            }
        }

        `when`("getTodayMenuList API가 실패하면") {
            coEvery {
                mealService.getTodayMeal("2025-01-01", Restaurant.HAKSIK.toString(), Time.LUNCH.toString())
            } returns ApiResult.UnknownError(IllegalStateException("boom"))

            then("빈 리스트를 반환한다") {
                runTest {
                    repository.getTodayMenuList("2025-01-01", Restaurant.HAKSIK, Time.LUNCH) shouldBe emptyList()
                }
            }
        }
    }
})
