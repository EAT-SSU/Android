package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.model.request.InquiriesRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface InquiresService {
    @POST("inquiries/") // 문의 작성
    fun inquireContent(
        @Body request: InquiriesRequestDto
    ): Call<BaseResponse<InquiriesRequestDto>>

}