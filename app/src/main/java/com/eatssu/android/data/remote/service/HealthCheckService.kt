package com.eatssu.android.data.remote.service

import retrofit2.Response
import retrofit2.http.GET

interface HealthCheckService {
    /**
     * 서버와 정상적으로 통신할 수 있는지 확인합니다.
     * 실제 서버의 상태(healthy)를 체크하는 목적이 아니라, 네트워크 연결이 가능한지 확인하는 용도입니다.
     */
    @GET("actuator/health")
    suspend fun checkHealth(): Response<Unit>
}
