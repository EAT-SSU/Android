package com.eatssu.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("reviewId") var reviewId: Long? = null,
    @SerializedName("reportType") var reportType: String? = null,
    @SerializedName("content") var content: String? = null
)
