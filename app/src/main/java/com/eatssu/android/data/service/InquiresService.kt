package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.InquiriesRequestDto
import com.eatssu.android.data.model.request.ReportRequestDto
import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.GetReportTypeResponseDto
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InquiresService {
    @POST("inquiries/") // 문의 작성
    fun inquireContent(
        @Body request: InquiriesRequestDto
    ): Call<BaseResponse<InquiriesRequestDto>>

    @GET("inquiries/{inquiryId}") //리뷰 신고 사유 받아오기
    fun getInquiries(
        @Path("userInquiriesId") userInquiriesId: Long,
    ): Call<GetReportTypeResponseDto>
}