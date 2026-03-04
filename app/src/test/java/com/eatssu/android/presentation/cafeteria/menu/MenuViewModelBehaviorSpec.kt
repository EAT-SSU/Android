package com.eatssu.android.presentation.cafeteria.menu

import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.usecase.menu.GetMenuListUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.UiState
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelBehaviorSpec : AppBehaviorSpec({

    given("메뉴 로드") {
        val useCase = mockk<GetMenuListUseCase>()

        `when`("식당 목록이 비어있으면") {
            val viewModel = MenuViewModel(useCase)

            then("빈 맵으로 성공 상태가 된다") {
                runTest {
                    viewModel.loadMenus(emptyList(), "20250101", Time.LUNCH)
                    advanceUntilIdle()

                    viewModel.uiState.value shouldBe UiState.Success(MenuState(emptyMap()))
                    coVerify(exactly = 0) { useCase(any(), any(), any()) }
                }
            }
        }

        `when`("여러 식당에 대한 조회가 성공하면") {
            val viewModel = MenuViewModel(useCase)
            val r1 = Restaurant.FOOD_COURT
            val r2 = Restaurant.HAKSIK
            val m1 = listOf(Menu(id = 1, name = "A", price = 1000, rate = 4.0))
            val m2 = listOf(Menu(id = 2, name = "B", price = 2000, rate = 3.5))
            coEvery { useCase(r1, "20250101", Time.LUNCH) } returns m1
            coEvery { useCase(r2, "20250101", Time.LUNCH) } returns m2

            then("식당별 메뉴 맵으로 성공 상태가 된다") {
                runTest {
                    viewModel.loadMenus(listOf(r1, r2), "20250101", Time.LUNCH)
                    advanceUntilIdle()

                    (viewModel.uiState.value is UiState.Success) shouldBe true
                    coVerify(exactly = 1) { useCase(r1, "20250101", Time.LUNCH) }
                    coVerify(exactly = 1) { useCase(r2, "20250101", Time.LUNCH) }
                }
            }
        }

        `when`("일부 식당 메뉴가 비어있어도") {
            val viewModel = MenuViewModel(useCase)
            val r1 = Restaurant.FOOD_COURT
            val r2 = Restaurant.HAKSIK
            val m1 = emptyList<Menu>()
            val m2 = listOf(Menu(id = 2, name = "B", price = 2000, rate = 3.5))
            coEvery { useCase(r1, "20250101", Time.DINNER) } returns m1
            coEvery { useCase(r2, "20250101", Time.DINNER) } returns m2

            then("성공 상태로 식당별 결과를 유지한다") {
                runTest {
                    viewModel.loadMenus(listOf(r1, r2), "20250101", Time.DINNER)
                    advanceUntilIdle()

                    viewModel.uiState.value shouldBe UiState.Success(
                        MenuState(
                            mapOf(
                                r1 to m1,
                                r2 to m2,
                            )
                        )
                    )
                    coVerify(exactly = 1) { useCase(r1, "20250101", Time.DINNER) }
                    coVerify(exactly = 1) { useCase(r2, "20250101", Time.DINNER) }
                }
            }
        }
    }
})
