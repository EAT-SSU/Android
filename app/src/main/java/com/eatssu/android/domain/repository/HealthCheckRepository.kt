package com.eatssu.android.domain.repository

interface HealthCheckRepository {
    suspend fun checkHealth(): Boolean
}
