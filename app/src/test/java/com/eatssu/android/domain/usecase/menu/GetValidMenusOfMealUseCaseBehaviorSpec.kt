package com.eatssu.android.domain.usecase.menu

import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class GetValidMenusOfMealUseCaseBehaviorSpec : AppBehaviorSpec({

    given("GetValidMenusOfMealUseCase") {
        val reviewRepository = mockk<ReviewRepository>()
        val useCase = GetValidMenusOfMealUseCase(reviewRepository)
        val menus = listOf(
            MenuMini(id = 1L, name = "제육"),
            MenuMini(id = 2L, name = "돈까스"),
        )

        `when`("유효 메뉴 목록 조회가 성공하면") {
            coEvery { reviewRepository.getValidMenusByMealId(10L) } returns menus

            then("동일 목록을 반환한다") {
                runTest {
                    useCase(10L) shouldBe menus
                    coVerify(exactly = 1) { reviewRepository.getValidMenusByMealId(10L) }
                }
            }
        }

        `when`("유효 메뉴 목록이 비어있으면") {
            coEvery { reviewRepository.getValidMenusByMealId(10L) } returns emptyList()

            then("빈 리스트를 반환한다") {
                runTest {
                    useCase(10L) shouldBe emptyList()
                }
            }
        }
    }
})
