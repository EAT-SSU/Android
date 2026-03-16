package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.request.ReportRequest
import com.eatssu.android.data.remote.service.ReportService
import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ReportRepositoryImplBehaviorSpec : AppBehaviorSpec({

    given("ReportRepositoryImpl") {
        val service = mockk<ReportService>()
        val repository = ReportRepositoryImpl(service)
        val request = ReportRequest(
            reviewId = 1L,
            reportType = "SPAM",
            content = "신고 사유",
        )

        `when`("신고 API가 성공하면") {
            coEvery { service.reportReview(request) } returns ApiResult.Success(Unit)

            then("true를 반환한다") {
                runTest {
                    repository.reportReview(request) shouldBe true
                }
            }
        }

        `when`("신고 API가 실패하면") {
            coEvery { service.reportReview(request) } returns ApiResult.UnknownError(IllegalStateException("boom"))

            then("false를 반환한다") {
                runTest {
                    repository.reportReview(request) shouldBe false
                }
            }
        }
    }
})
