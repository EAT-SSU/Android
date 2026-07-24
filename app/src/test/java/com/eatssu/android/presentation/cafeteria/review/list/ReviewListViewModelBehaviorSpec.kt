package com.eatssu.android.presentation.cafeteria.review.list

import androidx.paging.PagingData
import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetReviewListPagedUseCase
import com.eatssu.android.domain.usecase.review.GetReviewTranslationUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.expectToast
import com.eatssu.android.test.sampleReviewInfo
import com.eatssu.common.UiState
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.ToastType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewListViewModelBehaviorSpec : AppBehaviorSpec({

    given("리뷰 목록 화면") {
        val getReviewInfoUseCase = mockk<GetReviewInfoUseCase>()
        val getReviewListPagedUseCase = mockk<GetReviewListPagedUseCase>()
        val deleteReviewUseCase = mockk<DeleteReviewUseCase>()
        val getReviewTranslationUseCase = mockk<GetReviewTranslationUseCase>()

        every { getReviewListPagedUseCase(any(), any()) } returns flowOf(PagingData.empty())

        `when`("리뷰 정보를 정상 조회하면") {
            val viewModel = ReviewListViewModel(
                getReviewInfoUseCase,
                getReviewListPagedUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
            )
            val info = sampleReviewInfo()
            coEvery { getReviewInfoUseCase(MenuType.FIXED, 100L) } returns info

            then("Success 상태가 된다") {
                runTest {
                    viewModel.getReview(MenuType.FIXED, 100L)
                    advanceUntilIdle()

                    viewModel.uiState.value shouldBe UiState.Success(ReviewListState(info))
                }
            }
        }

        `when`("리뷰 정보 조회에서 예외가 발생하면") {
            val viewModel = ReviewListViewModel(
                getReviewInfoUseCase,
                getReviewListPagedUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
            )
            coEvery { getReviewInfoUseCase(MenuType.VARIABLE, 101L) } throws IllegalStateException("boom")

            then("Error 상태와 실패 토스트를 보낸다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.getReview(MenuType.VARIABLE, 101L)
                        advanceUntilIdle()

                        viewModel.uiState.value shouldBe UiState.Error
                        expectToast(R.string.toast_review_load_failed, ToastType.ERROR)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("리뷰 삭제가 실패하면") {
            val viewModel = ReviewListViewModel(
                getReviewInfoUseCase,
                getReviewListPagedUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
            )
            coEvery { deleteReviewUseCase(55L) } returns false

            then("실패 토스트를 보낸다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.deleteReview(55L)
                        advanceUntilIdle()

                        expectToast(R.string.toast_review_delete_failed, ToastType.ERROR)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("리뷰 삭제가 성공하면") {
            val viewModel = ReviewListViewModel(
                getReviewInfoUseCase,
                getReviewListPagedUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
            )
            coEvery { getReviewInfoUseCase(MenuType.FIXED, 300L) } returns sampleReviewInfo(count = 3)
            coEvery { deleteReviewUseCase(56L) } returns true

            then("ReviewDeleted 이벤트를 보내고 현재 파라미터로 정보를 다시 로드한다") {
                runTest {
                    viewModel.getReview(MenuType.FIXED, 300L)
                    advanceUntilIdle()

                    viewModel.uiEvent.test {
                        viewModel.deleteReview(56L)
                        advanceUntilIdle()

                        awaitItem() shouldBe ReviewListEvent.ReviewDeleted
                        coVerify(atLeast = 2) { getReviewInfoUseCase(MenuType.FIXED, 300L) }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("조회 파라미터 없이 리뷰 삭제가 성공하면") {
            val viewModel = ReviewListViewModel(
                getReviewInfoUseCase,
                getReviewListPagedUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
            )
            coEvery { deleteReviewUseCase(77L) } returns true

            then("ReviewDeleted 이벤트만 발생하고 정보 재조회는 하지 않는다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.deleteReview(77L)
                        advanceUntilIdle()

                        awaitItem() shouldBe ReviewListEvent.ReviewDeleted
                        coVerify(exactly = 0) { getReviewInfoUseCase(any(), any()) }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }
    }
})
