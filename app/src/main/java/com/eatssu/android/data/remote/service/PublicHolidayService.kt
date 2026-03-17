package com.eatssu.android.data.remote.service

import com.eatssu.android.data.remote.dto.response.PublicHolidayApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PublicHolidayService {

    /**
     * data.go.kr 공휴일(OpenAPI) 호출용 API.
     *
     * - 엔드포인트: SpcdeInfoService/getRestDeInfo
     * - ServiceKey는 이미 URL 인코딩된 값으로 전달한다(`encoded = true`).
     * - solYear/solMonth는 양력 기준 연/월이다.
     * - `_type=json`으로 JSON 응답을 받는다.
     */

    @GET("B090041/openapi/service/SpcdeInfoService/getRestDeInfo")
    suspend fun getRestDeInfo(
        @Query(value = "ServiceKey", encoded = true) serviceKey: String,
        @Query("solYear") solYear: String,
        @Query("solMonth") solMonth: String,
        @Query("numOfRows") numOfRows: Int = 50,
        @Query("pageNo") pageNo: Int = 1,
        @Query("_type") type: String = "json",
    ): PublicHolidayApiResponse
}
