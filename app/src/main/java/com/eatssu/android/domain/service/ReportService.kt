package com.eatssu.android.domain.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ReportRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("reports") //리뷰 신고하기
    suspend fun reportReview(
        @Body request: ReportRequest,
    ): BaseResponse<Void>
}