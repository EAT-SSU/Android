package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.remote.dto.request.ReportRequest
import com.eatssu.android.domain.repository.ReportRepository
import javax.inject.Inject

class PostReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
) {
    suspend operator fun invoke(body: ReportRequest): Boolean =
        reportRepository.reportReview(body)
}