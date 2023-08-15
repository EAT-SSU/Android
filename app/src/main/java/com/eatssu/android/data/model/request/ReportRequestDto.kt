package com.eatssu.android.data.model.request

import com.google.gson.annotations.SerializedName

data class ReportRequestDto(
    @SerializedName("reviewId")
    val reviewId: Long,

    @SerializedName("reportType")
    val reportType: String,

    @SerializedName("content")
    val content: String
)
