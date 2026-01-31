package com.eatssu.android.data.remote.service

import com.eatssu.android.data.remote.dto.request.ReportRequest
import com.eatssu.android.data.model.ApiResult
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("reports") //리뷰 신고하기
    suspend fun reportReview(
        @Body request: ReportRequest,
    ): ApiResult<Unit>
}