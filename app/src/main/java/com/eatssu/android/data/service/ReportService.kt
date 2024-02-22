package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.ReportRequestDto
import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.GetReportTypeResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReportService {
    @POST("reports/") //리뷰 신고하기
    fun reportReview(
        @Body request: ReportRequestDto
    ): Call<BaseResponse<Void>>

    @GET("reports/types") //리뷰 신고 사유 받아오기
    fun getMyReviews(): Call<GetReportTypeResponseDto>
}