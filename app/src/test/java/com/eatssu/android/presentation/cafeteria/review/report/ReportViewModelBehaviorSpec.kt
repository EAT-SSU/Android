package com.eatssu.android.presentation.cafeteria.review.report

import com.eatssu.android.R
import com.eatssu.android.domain.usecase.review.PostReportUseCase
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.asStringResIdOrNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelBehaviorSpec : AppBehaviorSpec({

    given("신고 전송") {
        val postReportUseCase = mockk<PostReportUseCase>()

        `when`("신고가 실패하면") {
            coEvery { postReportUseCase(any(), any(), any()) } returns false
            val viewModel = ReportViewModel(postReportUseCase)

            then("error=true와 실패 토스트를 설정한다") {
                runTest {
                    viewModel.postData(1L, "COPY", "bad")
                    advanceUntilIdle()

                    viewModel.uiState.value.loading shouldBe false
                    viewModel.uiState.value.error shouldBe true
                    viewModel.uiState.value.toastMessage.asStringResIdOrNull() shouldBe R.string.toast_report_failed
                    viewModel.uiState.value.isDone shouldBe false

                    coVerify { postReportUseCase(reviewId = 1L, reportType = "COPY", content = "bad") }
                }
            }
        }

        `when`("신고가 성공하면") {
            coEvery { postReportUseCase(any(), any(), any()) } returns true
            val viewModel = ReportViewModel(postReportUseCase)

            then("isDone=true와 성공 토스트를 설정한다") {
                runTest {
                    viewModel.postData(2L, "EXTRA", "spam")
                    advanceUntilIdle()

                    viewModel.uiState.value.loading shouldBe false
                    viewModel.uiState.value.error shouldBe false
                    viewModel.uiState.value.toastMessage.asStringResIdOrNull() shouldBe R.string.toast_report_success
                    viewModel.uiState.value.isDone shouldBe true
                }
            }
        }
    }
})
