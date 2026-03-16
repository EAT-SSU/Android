package com.eatssu.android.domain.usecase.review

import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.sampleReviewInfo
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.common.enums.MenuType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class GetReviewInfoUseCaseBehaviorSpec : AppBehaviorSpec({

    given("리뷰 통계 조회") {
        val reviewRepository = mockk<ReviewRepository>()
        val useCase = GetReviewInfoUseCase(reviewRepository)

        `when`("고정 메뉴 리뷰 통계를 조회하면") {
            val info = sampleReviewInfo(count = 10, rating = 4.2)
            coEvery { reviewRepository.getMenuReviewInfo(1L) } returns info

            then("menu 리뷰 정보를 반환한다") {
                runTest {
                    useCase(MenuType.FIXED, 1L) shouldBe info
                    coVerify(exactly = 1) { reviewRepository.getMenuReviewInfo(1L) }
                    coVerify(exactly = 0) { reviewRepository.getMealReviewInfo(any()) }
                }
            }
        }

        `when`("변동 메뉴 리뷰 통계를 조회하면") {
            val info = sampleReviewInfo(count = 5, rating = 3.8)
            coEvery { reviewRepository.getMealReviewInfo(2L) } returns info

            then("meal 리뷰 정보를 반환한다") {
                runTest {
                    useCase(MenuType.VARIABLE, 2L) shouldBe info
                    coVerify(exactly = 1) { reviewRepository.getMealReviewInfo(2L) }
                    coVerify(exactly = 0) { reviewRepository.getMenuReviewInfo(any()) }
                }
            }
        }

        `when`("저장소가 null을 반환하면") {
            coEvery { reviewRepository.getMealReviewInfo(3L) } returns null

            then("null을 그대로 반환한다") {
                runTest {
                    useCase(MenuType.VARIABLE, 3L) shouldBe null
                }
            }
        }
    }
})
