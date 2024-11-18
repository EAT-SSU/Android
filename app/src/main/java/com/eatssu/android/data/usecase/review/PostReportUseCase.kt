package com.eatssu.android.data.usecase.review

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ReportRequest
import com.eatssu.android.data.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
) {
    suspend operator fun invoke(body: ReportRequest): Flow<BaseResponse<Void>> =
        reportRepository.reportReview(body)
}