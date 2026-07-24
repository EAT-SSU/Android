package com.eatssu.android.presentation.mypage.myreview

import app.cash.turbine.test
import com.eatssu.android.R
import com.eatssu.android.domain.model.ReviewTranslation
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetMyReviewsUseCase
import com.eatssu.android.domain.usecase.review.GetReviewTranslationUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.assertToast
import com.eatssu.android.test.awaitToastEvent
import com.eatssu.android.test.sampleReview
import com.eatssu.common.UiState
import com.eatssu.common.enums.ToastType
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class MyReviewViewModelBehaviorSpec : AppBehaviorSpec({

    given("내 리뷰 화면") {
        val getMyReviewsUseCase = mockk<GetMyReviewsUseCase>()
        val getUserNickNameUseCase = mockk<GetUserNickNameUseCase>()
        val deleteReviewUseCase = mockk<DeleteReviewUseCase>()
        val getReviewTranslationUseCase = mockk<GetReviewTranslationUseCase>()
        val getAccessTokenUseCase = mockk<GetAccessTokenUseCase>()

        every { getAccessTokenUseCase() } returns "access-token"

        `when`("리뷰 목록이 비어있으면") {
            coEvery { getMyReviewsUseCase() } returns emptyList()
            val viewModel = MyReviewViewModel(
                getMyReviewsUseCase,
                getUserNickNameUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
                getAccessTokenUseCase,
            )

            then("NoReview 상태가 된다") {
                runTest {
                    advanceUntilIdle()
                    viewModel.uiState.value shouldBe UiState.Success(MyReviewState.NoReview)
                }
            }
        }

        `when`("리뷰 목록이 있으면") {
            val review = sampleReview()
            coEvery { getMyReviewsUseCase() } returns listOf(review)
            val viewModel = MyReviewViewModel(
                getMyReviewsUseCase,
                getUserNickNameUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
                getAccessTokenUseCase,
            )

            then("ReviewExists 상태가 된다") {
                runTest {
                    advanceUntilIdle()
                    viewModel.uiState.value shouldBe UiState.Success(MyReviewState.ReviewExists(listOf(review)))
                }
            }
        }

        `when`("닉네임 로드를 호출하면") {
            coEvery { getMyReviewsUseCase() } returns emptyList()
            coEvery { getUserNickNameUseCase() } returns "nickname"
            val viewModel = MyReviewViewModel(
                getMyReviewsUseCase,
                getUserNickNameUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
                getAccessTokenUseCase,
            )

            then("닉네임 stateFlow를 업데이트한다") {
                runTest {
                    viewModel.loadUserNickname()
                    advanceUntilIdle()
                    viewModel.nickname.value shouldBe "nickname"
                }
            }
        }

        `when`("리뷰 삭제가 실패하면") {
            coEvery { getMyReviewsUseCase() } returns emptyList()
            coEvery { deleteReviewUseCase(10L) } returns false
            val viewModel = MyReviewViewModel(
                getMyReviewsUseCase,
                getUserNickNameUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
                getAccessTokenUseCase,
            )

            then("실패 토스트를 보낸다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.deleteReview(10L)
                        advanceUntilIdle()

                        awaitToastEvent().assertToast(R.string.toast_review_delete_failed, ToastType.ERROR)
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("리뷰 삭제가 성공하면") {
            val review = sampleReview(id = 2L)
            coEvery { getMyReviewsUseCase() } returnsMany listOf(listOf(review), emptyList())
            coEvery { deleteReviewUseCase(2L) } returns true
            val viewModel = MyReviewViewModel(
                getMyReviewsUseCase,
                getUserNickNameUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
                getAccessTokenUseCase,
            )

            then("성공 토스트 후 목록을 재조회한다") {
                runTest {
                    viewModel.uiEvent.test {
                        viewModel.deleteReview(2L)
                        advanceUntilIdle()

                        awaitToastEvent().assertToast(R.string.toast_review_delete_success, ToastType.SUCCESS)
                        coVerify(atLeast = 2) { getMyReviewsUseCase() }
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        `when`("번역 결과가 캐시되어 있으면") {
            val review = sampleReview(id = 255L, content = "맛있어요")
            coEvery { getMyReviewsUseCase() } returns listOf(review)
            coEvery { getReviewTranslationUseCase(255L, "EN") } returns ReviewTranslation(
                reviewId = 255L,
                language = "EN",
                translatedContent = "It was delicious.",
                cached = true,
            )
            val viewModel = MyReviewViewModel(
                getMyReviewsUseCase,
                getUserNickNameUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
                getAccessTokenUseCase,
            )

            then("원문과 번역을 전환해도 번역 API는 한 번만 호출한다") {
                runTest {
                    advanceUntilIdle()

                    viewModel.toggleReviewTranslation(review, "EN")
                    advanceUntilIdle()
                    viewModel.translationStates.value[255L]?.isTranslated shouldBe true

                    viewModel.toggleReviewTranslation(review, "EN")
                    viewModel.translationStates.value[255L]?.isTranslated shouldBe false

                    viewModel.toggleReviewTranslation(review, "EN")
                    viewModel.translationStates.value[255L]?.isTranslated shouldBe true
                    coVerify(exactly = 1) { getReviewTranslationUseCase(255L, "EN") }
                }
            }
        }

        `when`("번역 결과가 원문과 같으면") {
            val review = sampleReview(id = 256L, content = "얌")
            coEvery { getMyReviewsUseCase() } returns listOf(review)
            coEvery { getReviewTranslationUseCase(256L, "EN") } returns ReviewTranslation(
                reviewId = 256L,
                language = "EN",
                translatedContent = "얌",
                cached = true,
            )
            val viewModel = MyReviewViewModel(
                getMyReviewsUseCase,
                getUserNickNameUseCase,
                deleteReviewUseCase,
                getReviewTranslationUseCase,
                getAccessTokenUseCase,
            )

            then("안내 토스트를 표시하고 번역을 비활성화한다") {
                runTest {
                    viewModel.toggleReviewTranslation(review, "EN")
                    advanceUntilIdle()

                    viewModel.translationStates.value[256L]?.isUnavailable shouldBe true
                }
            }
        }
    }
})
