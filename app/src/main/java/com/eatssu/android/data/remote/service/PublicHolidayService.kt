package com.eatssu.android.data.remote.service

import com.eatssu.android.data.remote.dto.response.PublicHolidayApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PublicHolidayService {

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
