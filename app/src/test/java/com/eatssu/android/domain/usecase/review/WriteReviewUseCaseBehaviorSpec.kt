package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.MenuType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class WriteReviewUseCaseBehaviorSpec : AppBehaviorSpec({

    given("리뷰 작성 유즈케이스") {
        val reviewRepository = mockk<ReviewRepository>()
        val useCase = WriteReviewUseCase(reviewRepository)

        `when`("FIXED 메뉴에 이미지 없이 작성하면") {
            coEvery {
                reviewRepository.writeMenuReview(
                    menuId = 100L,
                    rating = 5,
                    content = "good",
                    imageUrls = emptyList(),
                    likeMenuIdList = listOf(1L),
                )
            } returns true

            then("writeMenuReview를 호출하고 결과를 반환한다") {
                runTest {
                    useCase(
                        menuType = MenuType.FIXED,
                        itemId = 100L,
                        rating = 5,
                        content = "good",
                        imageUrl = null,
                        likeMenuIdList = listOf(1L),
                    ) shouldBe true

                    coVerify(exactly = 1) {
                        reviewRepository.writeMenuReview(
                            menuId = 100L,
                            rating = 5,
                            content = "good",
                            imageUrls = emptyList(),
                            likeMenuIdList = listOf(1L),
                        )
                    }
                }
            }
        }

        `when`("FIXED 메뉴에 이미지가 있으면") {
            coEvery {
                reviewRepository.writeMenuReview(
                    menuId = 100L,
                    rating = 4,
                    content = "",
                    imageUrls = listOf("https://img"),
                    likeMenuIdList = null,
                )
            } returns false

            then("이미지 URL을 리스트로 전달한다") {
                runTest {
                    useCase(
                        menuType = MenuType.FIXED,
                        itemId = 100L,
                        rating = 4,
                        content = "",
                        imageUrl = "https://img",
                        likeMenuIdList = null,
                    ) shouldBe false
                }
            }
        }

        `when`("VARIABLE 메뉴에 이미지 없이 작성하면") {
            coEvery {
                reviewRepository.writeMealReview(
                    mealId = 77L,
                    rating = 3,
                    content = "ok",
                    imageUrls = emptyList(),
                    likeMenuIdList = emptyList(),
                )
            } returns true

            then("writeMealReview를 호출하고 결과를 반환한다") {
                runTest {
                    useCase(
                        menuType = MenuType.VARIABLE,
                        itemId = 77L,
                        rating = 3,
                        content = "ok",
                        imageUrl = null,
                        likeMenuIdList = emptyList(),
                    ) shouldBe true
                }
            }
        }

        `when`("VARIABLE 메뉴에 이미지가 있으면") {
            coEvery {
                reviewRepository.writeMealReview(
                    mealId = 88L,
                    rating = 2,
                    content = "bad",
                    imageUrls = listOf("https://img2"),
                    likeMenuIdList = listOf(9L),
                )
            } returns false

            then("mealId와 이미지 리스트를 전달한다") {
                runTest {
                    useCase(
                        menuType = MenuType.VARIABLE,
                        itemId = 88L,
                        rating = 2,
                        content = "bad",
                        imageUrl = "https://img2",
                        likeMenuIdList = listOf(9L),
                    ) shouldBe false
                }
            }
        }
    }
})
