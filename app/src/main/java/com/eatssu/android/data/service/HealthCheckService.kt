package com.eatssu.android.data.service

import com.eatssu.android.data.model.ApiResult
import retrofit2.http.GET

interface HealthCheckService {
    @GET("actuator/health")
    suspend fun checkHealth(): ApiResult<Unit>
}

