package com.eatssu.android.presentation.cafeteria.menu

import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.model.MenuLoadResult
import com.eatssu.android.domain.usecase.menu.LoadMenusUseCase
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
import java.time.LocalDate
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelBehaviorSpec : AppBehaviorSpec({

    given("메뉴 로드") {
        val useCase = mockk<LoadMenusUseCase>()
        val clock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC)
        val date = LocalDate.now(clock)

        `when`("usecase가 빈 결과를 반환하면") {
            val viewModel = MenuViewModel(useCase)
            coEvery { useCase(date, Time.LUNCH) } returns MenuLoadResult(
                menuMap = emptyMap(),
            )

            then("빈 맵으로 성공 상태가 된다") {
                runTest {
                    viewModel.loadMenus(date, Time.LUNCH)
                    advanceUntilIdle()

                    viewModel.uiState.value shouldBe UiState.Success(MenuState(emptyMap()))
                    coVerify(exactly = 1) { useCase(date, Time.LUNCH) }
                }
            }
        }

        `when`("usecase가 식당별 메뉴 맵을 반환하면") {
            val viewModel = MenuViewModel(useCase)
            val r1 = Restaurant.FOOD_COURT
            val r2 = Restaurant.HAKSIK
            val m1 = listOf(Menu(id = 1, name = "A", price = 1000, rate = 4.0))
            val m2 = listOf(Menu(id = 2, name = "B", price = 2000, rate = 3.5))
            coEvery { useCase(date, Time.LUNCH) } returns MenuLoadResult(
                menuMap = mapOf(
                    r1 to m1,
                    r2 to m2,
                ),
            )

            then("식당별 메뉴 맵으로 성공 상태가 된다") {
                runTest {
                    viewModel.loadMenus(date, Time.LUNCH)
                    advanceUntilIdle()

                    viewModel.uiState.value shouldBe UiState.Success(
                        MenuState(
                            menuMap = mapOf(
                                r1 to m1,
                                r2 to m2,
                            ),
                        ),
                    )
                    coVerify(exactly = 1) { useCase(date, Time.LUNCH) }
                }
            }
        }

        `when`("일부 식당 메뉴가 비어있어도") {
            val viewModel = MenuViewModel(useCase)
            val r1 = Restaurant.FOOD_COURT
            val r2 = Restaurant.HAKSIK
            val m1 = emptyList<Menu>()
            val m2 = listOf(Menu(id = 2, name = "B", price = 2000, rate = 3.5))
            coEvery { useCase(date, Time.DINNER) } returns MenuLoadResult(
                menuMap = mapOf(
                    r1 to m1,
                    r2 to m2,
                ),
            )

            then("성공 상태로 식당별 결과를 유지한다") {
                runTest {
                    viewModel.loadMenus(date, Time.DINNER)
                    advanceUntilIdle()

                    viewModel.uiState.value shouldBe UiState.Success(
                        MenuState(
                            menuMap = mapOf(
                                r1 to m1,
                                r2 to m2,
                            ),
                        )
                    )
                    coVerify(exactly = 1) { useCase(date, Time.DINNER) }
                }
            }
        }
    }
})
