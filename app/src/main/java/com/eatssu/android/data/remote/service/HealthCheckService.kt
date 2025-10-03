package com.eatssu.android.data.service

import com.eatssu.android.data.model.ApiResult
import retrofit2.http.GET

interface HealthCheckService {
    /**
     * 서버와 정상적으로 통신할 수 있는지 확인합니다.
     * 실제 서버의 상태(healthy)를 체크하는 목적이 아니라, 네트워크 연결이 가능한지 확인하는 용도입니다.
     * 반환 타입이 Unit인 이유는 응답 본문의 내용이 중요하지 않고, 통신 성공 여부만 판단하기 때문입니다.
     */
    @GET("actuator/health")
    suspend fun checkHealth(): ApiResult<Unit>
}
