package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.InquiryRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface InquiryService {
    @POST("inquiries/") // 문의 작성
    suspend fun inquireContent(
        @Body request: InquiryRequest,
    ): BaseResponse<Void>

}