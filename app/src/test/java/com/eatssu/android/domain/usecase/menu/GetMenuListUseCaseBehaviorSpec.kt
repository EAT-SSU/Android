package com.eatssu.android.domain.usecase.menu

import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.repository.MealRepository
import com.eatssu.android.domain.repository.MenuRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class GetMenuListUseCaseBehaviorSpec : AppBehaviorSpec({

    given("메뉴 목록 조회") {
        val menuRepository = mockk<MenuRepository>()
        val mealRepository = mockk<MealRepository>()
        val useCase = GetMenuListUseCase(menuRepository, mealRepository)

        `when`("고정식당 메뉴를 조회하면") {
            val result = listOf(Menu(id = 1, name = "돈까스", price = 5000, rate = 4.0))
            coEvery { menuRepository.getFixedMenuList(Restaurant.SNACK_CORNER) } returns result

            then("menuRepository.getFixedMenuList를 사용한다") {
                runTest {
                    useCase(Restaurant.SNACK_CORNER, "2025-01-01", Time.LUNCH) shouldBe result
                    coVerify(exactly = 1) { menuRepository.getFixedMenuList(Restaurant.SNACK_CORNER) }
                    coVerify(exactly = 0) { mealRepository.getTodayMenuList(any(), any(), any()) }
                }
            }
        }

        `when`("변동식당 메뉴를 조회하면") {
            val result = listOf(Menu(id = 2, name = "비빔밥", price = 4500, rate = 3.5))
            coEvery {
                mealRepository.getTodayMenuList("2025-01-01", Restaurant.HAKSIK, Time.DINNER)
            } returns result

            then("mealRepository.getTodayMenuList를 사용한다") {
                runTest {
                    useCase(Restaurant.HAKSIK, "2025-01-01", Time.DINNER) shouldBe result
                    coVerify(exactly = 1) {
                        mealRepository.getTodayMenuList("2025-01-01", Restaurant.HAKSIK, Time.DINNER)
                    }
                    coVerify(exactly = 0) { menuRepository.getFixedMenuList(any()) }
                }
            }
        }
    }
})
