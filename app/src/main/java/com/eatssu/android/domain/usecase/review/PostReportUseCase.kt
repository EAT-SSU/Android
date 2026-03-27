package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.repository.ReportRepository
import javax.inject.Inject

class PostReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
) {
    suspend operator fun invoke(
        reviewId: Long,
        reportType: String,
        content: String
    ): Boolean =
        reportRepository.reportReview(
            reviewId = reviewId,
            reportType = reportType,
            content = content,
        )
}
