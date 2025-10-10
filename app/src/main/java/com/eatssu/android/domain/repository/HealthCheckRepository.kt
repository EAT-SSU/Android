package com.eatssu.android.domain.repository

interface HealthCheckRepository {
    suspend fun checkServerHealth(): Boolean
}

