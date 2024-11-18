package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.request.ReportRequest
import com.eatssu.android.data.dto.response.BaseResponse
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    suspend fun reportReview(
        body: ReportRequest,
    ): Flow<BaseResponse<Void>>

}

