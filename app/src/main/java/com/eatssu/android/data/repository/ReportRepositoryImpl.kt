package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.request.ReportRequest
import com.eatssu.android.data.model.isSuccess
import com.eatssu.android.data.service.ReportService
import com.eatssu.android.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(private val reportService: ReportService) :
    ReportRepository {

    override suspend fun reportReview(body: ReportRequest): Boolean =
        reportService.reportReview(body).isSuccess()

}
