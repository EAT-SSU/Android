package com.eatssu.android.data.service

import com.eatssu.android.data.dto.request.ReportRequest
import com.eatssu.android.data.dto.response.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("reports") //리뷰 신고하기
    suspend fun reportReview(
        @Body request: ReportRequest,
    ): BaseResponse<Void>
}