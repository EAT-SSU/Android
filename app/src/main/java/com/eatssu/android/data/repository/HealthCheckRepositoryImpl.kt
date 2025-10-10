package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.response.HealthCheckResponse
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.service.HealthCheckService
import com.eatssu.android.domain.repository.HealthCheckRepository
import javax.inject.Inject

class HealthCheckRepositoryImpl @Inject constructor(
    private val healthCheckService: HealthCheckService
) : HealthCheckRepository {
    override suspend fun checkServerHealth(): ApiResult<HealthCheckResponse> =
        healthCheckService.checkHealth()
}

