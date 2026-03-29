package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.isSuccess
import com.eatssu.android.data.remote.dto.request.ReportRequest
import com.eatssu.android.data.remote.service.ReportService
import com.eatssu.android.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(private val reportService: ReportService) :
    ReportRepository {

    override suspend fun reportReview(
        reviewId: Long,
        reportType: String,
        content: String,
    ): Boolean =
        reportService.reportReview(
            ReportRequest(
                reviewId = reviewId,
                reportType = reportType,
                content = content
            )
        ).isSuccess()

}
