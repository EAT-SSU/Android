package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.remote.service.HealthCheckService
import com.eatssu.android.domain.repository.HealthCheckRepository
import com.eatssu.android.presentation.base.NetworkErrorEventBus
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class HealthCheckRepositoryImpl @Inject constructor(
    private val healthCheckService: HealthCheckService
) : HealthCheckRepository {
    override suspend fun checkHealth(): Boolean {
        return try {
            val response = healthCheckService.checkHealth()
            return response.isSuccessful
        } catch (e: IOException) {
            NetworkErrorEventBus.notifyNetworkError()
            Timber.e(e, "Health check network error")
            false
        } catch (e: Exception) {
            Timber.e(e, "Health check failed")
            false
        }
    }
}
