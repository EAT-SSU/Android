package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.request.ReportRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.service.ReportService
import com.eatssu.android.domain.repository.ReportRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(private val reportService: ReportService) :
    ReportRepository {

    override suspend fun reportReview(body: ReportRequest): Flow<BaseResponse<Void>> =
        flow {
            emit(reportService.reportReview(body))
        }

}
