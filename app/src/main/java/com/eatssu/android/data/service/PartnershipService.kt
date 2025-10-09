package com.eatssu.android.data.service

import com.eatssu.android.data.dto.response.PartnershipResponse
import com.eatssu.android.data.dto.response.PartnershipRestaurantResponse
import com.eatssu.android.data.model.ApiResult
import retrofit2.http.GET

interface PartnershipService{

    // 전체 제휴 조회
    @GET("partnerships")
    suspend fun getAllPartnerships(): ApiResult<List<PartnershipResponse>>

    // 개별 제휴 조회
    @GET("partnerships/{partnershipId}")
    suspend fun getPartnershipById(partnershipId: Int): ApiResult<PartnershipRestaurantResponse>

    // TODO 제휴 찜/등록하기/ 취소하기
    @GET("partnerships/{partnershipId}/like")
    suspend fun likePartnership(partnershipId: Int): ApiResult<Unit>

}