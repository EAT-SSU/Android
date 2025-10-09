package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.request.ReportRequest

interface ReportRepository {
    suspend fun reportReview(
        body: ReportRequest,
    ): Boolean
}

