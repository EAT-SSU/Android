package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.ReportRequest
import com.eatssu.android.data.model.response.GetReportTypeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportService {
    @POST("report") //리뷰 신고하기
    fun reportReview(
        @Path("reviewId") reviewId: Int,
        @Path("reportType") reportType: String,
        @Path("content") content: String
    )
    : Call<ReportRequest>

    @GET("report/type") //리뷰 신고 사유 받아오기
    fun getMyReviews(): Call<GetReportTypeResponse>
}