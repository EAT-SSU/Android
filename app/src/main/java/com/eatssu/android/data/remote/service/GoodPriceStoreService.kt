package com.eatssu.android.data.remote.service

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.GoodPriceStoreDetailResponse
import com.eatssu.android.data.remote.dto.response.GoodPriceStoreResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 착한가격업소 관련 API Retrofit Service (비로그인 사용자도 접근 가능)
 */
interface GoodPriceStoreService {

    // 업종별 착한가격업소 목록 조회 (category 미지정 시 전체 조회)
    @GET("good-price-stores")
    suspend fun getStores(
        @Query("category") category: String? = null,
    ): ApiResult<List<GoodPriceStoreResponse>>

    // 착한가격업소 단일 상세 정보 조회
    @GET("good-price-stores/{id}")
    suspend fun getStoreDetail(
        @Path("id") id: Long,
    ): ApiResult<GoodPriceStoreDetailResponse>
}
