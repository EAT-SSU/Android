package com.eatssu.android.domain.repository

import com.eatssu.android.data.remote.dto.request.ReportRequest
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun reportReview(
        body: ReportRequest,
    ): Boolean
}

