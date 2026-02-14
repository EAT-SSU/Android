package com.eatssu.android.presentation.cafeteria.review.modify

import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.usecase.review.ModifyReviewUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.assertToast
import com.eatssu.android.test.awaitToastEvent
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.ToastType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ModifyViewModelBehaviorSpec : AppBehaviorSpec({

    val likes = listOf(
        Review.MenuLikeInfo(menuId = 1L, name = "A", isLike = true),
        Review.MenuLikeInfo(menuId = 2L, name = "B", isLike = false),
    )

    given("리뷰 수정 폼") {
        val useCase = mockk<ModifyReviewUseCase>()

        `when`("init을 호출하면") {
            val viewModel = ModifyViewModel(useCase)

            then("Editing 상태와 baseline이 초기화된다") {
                viewModel.init(4, "old", likes)

                viewModel.uiState.value shouldBe UiState.Success(
                    ModifyState.Editing(
                        rating = 4,
                        content = "old",
                        menuLikeInfos = likes,
                        baseline = ModifyState.Baseline(4, "old", likes),
                    )
                )
            }
        }

        `when`("변경사항이 없거나 rating이 0이면 submit하면") {
            val viewModel = ModifyViewModel(useCase)
            viewModel.init(4, "old", likes)

            then("아무 요청도 보내지 않는다") {
                runTest {
                    viewModel.submit(9L)
                    advanceUntilIdle()
                    coVerify(exactly = 0) { useCase(any(), any(), any(), any()) }
                }
            }

            then("rating을 0으로 바꿔도 요청하지 않는다") {
                runTest {
                    viewModel.onRatingChanged(0)
                    viewModel.submit(9L)
                    advanceUntilIdle()
                    coVerify(exactly = 0) { useCase(any(), any(), any(), any()) }
                }
            }
        }

        `when`("수정이 성공하면") {
            val viewModel = ModifyViewModel(useCase)
            viewModel.init(4, "old", likes)
            viewModel.onContentChanged("new")
            coEvery { useCase(10L, 4, "new", any()) } returns true

            then("뒤로가기와 성공 토스트를 보낸다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.submit(10L)
                        advanceUntilIdle()

                        awaitItem() shouldBe UiEvent.NavigateBack
                        awaitToastEvent().assertToast(R.string.toast_review_modify_success, ToastType.SUCCESS)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("수정이 실패하면") {
            val useCase2 = mockk<ModifyReviewUseCase>()
            val viewModel = ModifyViewModel(useCase2)
            viewModel.init(4, "old", likes)
            viewModel.onContentChanged("new")
            coEvery { useCase2(11L, 4, "new", any()) } returns false

            then("현재 동작(characterization): 실패 토스트 후에도 뒤로가기+성공 토스트를 보낸다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.submit(11L)
                        advanceUntilIdle()

                        awaitToastEvent().assertToast(R.string.toast_review_modify_failed, ToastType.ERROR)
                        awaitItem() shouldBe UiEvent.NavigateBack
                        awaitToastEvent().assertToast(R.string.toast_review_modify_success, ToastType.SUCCESS)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }
    }
})
