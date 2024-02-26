package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ReportRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ReportService {
    @POST("reports/") //리뷰 신고하기
    fun reportReview(
        @Body request: ReportRequestDto
    ): Call<BaseResponse<Void>>
}