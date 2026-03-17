package com.eatssu.android.domain.usecase.review

import androidx.paging.PagingData
import com.eatssu.android.data.remote.dto.request.ReportRequest
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.ReportRepository
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.sampleReview
import com.eatssu.common.enums.MenuType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewDelegatingUseCasesBehaviorSpec : AppBehaviorSpec({

    given("DeleteReviewUseCase") {
        val reviewRepository = mockk<ReviewRepository>()
        val useCase = DeleteReviewUseCase(reviewRepository)

        `when`("삭제 요청이 성공하면") {
            coEvery { reviewRepository.deleteReview(11L) } returns true

            then("true를 반환한다") {
                runTest {
                    useCase(11L) shouldBe true
                    coVerify(exactly = 1) { reviewRepository.deleteReview(11L) }
                }
            }
        }

        `when`("삭제 요청이 실패하면") {
            coEvery { reviewRepository.deleteReview(11L) } returns false

            then("false를 반환한다") {
                runTest {
                    useCase(11L) shouldBe false
                }
            }
        }
    }

    given("GetImageUrlUseCase") {
        val reviewRepository = mockk<ReviewRepository>()
        val useCase = GetImageUrlUseCase(reviewRepository)
        val file = File("image.jpg")

        `when`("이미지 업로드 URL이 존재하면") {
            coEvery { reviewRepository.getImageString(file) } returns "https://img"

            then("URL을 그대로 반환한다") {
                runTest {
                    useCase(file) shouldBe "https://img"
                }
            }
        }

        `when`("이미지 업로드가 실패하면") {
            coEvery { reviewRepository.getImageString(file) } returns null

            then("null을 반환한다") {
                runTest {
                    useCase(file) shouldBe null
                }
            }
        }
    }

    given("GetMyReviewsUseCase") {
        val reviewRepository = mockk<ReviewRepository>()
        val useCase = GetMyReviewsUseCase(reviewRepository)
        val reviews = listOf(sampleReview(id = 1L), sampleReview(id = 2L))

        `when`("repository가 내 리뷰 목록을 반환하면") {
            coEvery { reviewRepository.getMyReviews() } returns reviews

            then("동일 목록을 반환한다") {
                runTest {
                    useCase() shouldBe reviews
                }
            }
        }
    }

    given("GetReviewListPagedUseCase") {
        val reviewRepository = mockk<ReviewRepository>()
        val useCase = GetReviewListPagedUseCase(reviewRepository)
        val menuFlow = flowOf(PagingData.empty<Review>())
        val mealFlow = flowOf(PagingData.empty<Review>())

        every { reviewRepository.getMenuReviewListPaged(10L) } returns menuFlow
        every { reviewRepository.getMealReviewListPaged(20L) } returns mealFlow

        `when`("menuType이 FIXED면") {
            then("고정 메뉴 paging flow를 반환한다") {
                useCase(MenuType.FIXED, 10L) shouldBe menuFlow
            }
        }

        `when`("menuType이 VARIABLE면") {
            then("변동 메뉴 paging flow를 반환한다") {
                useCase(MenuType.VARIABLE, 20L) shouldBe mealFlow
            }
        }
    }

    given("ModifyReviewUseCase") {
        val reviewRepository = mockk<ReviewRepository>()
        val useCase = ModifyReviewUseCase(reviewRepository)
        val menuLikes = listOf(Review.MenuLikeInfo(1L, "A", true))

        `when`("수정 요청이 성공하면") {
            coEvery { reviewRepository.modifyReview(1L, 5, "content", menuLikes) } returns true

            then("true를 반환한다") {
                runTest {
                    useCase(1L, 5, "content", menuLikes) shouldBe true
                }
            }
        }

        `when`("수정 요청이 실패하면") {
            coEvery { reviewRepository.modifyReview(1L, 5, "content", menuLikes) } returns false

            then("false를 반환한다") {
                runTest {
                    useCase(1L, 5, "content", menuLikes) shouldBe false
                }
            }
        }
    }

    given("PostReportUseCase") {
        val reportRepository = mockk<ReportRepository>()
        val useCase = PostReportUseCase(reportRepository)
        val body = ReportRequest(
            reviewId = 3L,
            reportType = "SPAM",
            content = "신고 사유",
        )

        `when`("신고가 성공하면") {
            coEvery { reportRepository.reportReview(body) } returns true

            then("true를 반환한다") {
                runTest {
                    useCase(body) shouldBe true
                }
            }
        }

        `when`("신고가 실패하면") {
            coEvery { reportRepository.reportReview(body) } returns false

            then("false를 반환한다") {
                runTest {
                    useCase(body) shouldBe false
                }
            }
        }
    }
})
