package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.isSuccess
import com.eatssu.android.data.remote.service.HealthCheckService
import com.eatssu.android.domain.repository.HealthCheckRepository
import javax.inject.Inject

class HealthCheckRepositoryImpl @Inject constructor(
    private val healthCheckService: HealthCheckService
) : HealthCheckRepository {
    override suspend fun checkHealth(): Boolean {
        return healthCheckService.checkHealth().isSuccess()
    }
}
