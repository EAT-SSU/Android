package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.response.HealthCheckResponse
import com.eatssu.android.data.model.ApiResult

interface HealthCheckRepository {
    suspend fun checkServerHealth(): ApiResult<HealthCheckResponse>
}

