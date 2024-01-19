package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.ReportRequestDto
import com.eatssu.android.data.model.response.GetReportTypeResponseDto
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InquiresService {
    @POST("inquires/") //리뷰 신고하기
    fun inquireContent(
        @Body request: String
    ): Call<Void>

    @GET("inquires/{userInquiriesId}") //리뷰 신고 사유 받아오기
    fun getInquiries(
        @Path("userInquiriesId") userInquiriesId: Long,
    ): Call<GetReportTypeResponseDto>
}