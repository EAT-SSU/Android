package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.request.ReportRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostReportUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
) {
    suspend operator fun invoke(body: ReportRequest): Flow<BaseResponse<Void>> =
        reportRepository.reportReview(body)
}