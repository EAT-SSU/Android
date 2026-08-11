package com.eatssu.android.data.remote.service

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.PartnershipResponse
import com.eatssu.android.data.remote.dto.response.PartnershipRestaurantResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PartnershipService {

    // 전체 제휴 조회
    @GET("partnerships")
    suspend fun getAllPartnerships(): ApiResult<List<PartnershipResponse>>

    // 개별 제휴 조회
    @GET("partnerships/{partnershipId}")
    suspend fun getPartnershipById(
        @Path("partnershipId") partnershipId: Int
    ): ApiResult<PartnershipRestaurantResponse>

    // TODO 제휴 찜/등록하기/ 취소하기
    @POST("partnerships/{partnershipId}/like")
    suspend fun likePartnership(
        @Path("partnershipId") partnershipId: Int
    ): ApiResult<Unit>

}