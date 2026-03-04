package com.eatssu.android.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
    @SerialName("reviewId")
    val reviewId: Long,

    @SerialName("reportType")
    val reportType: String,

    @SerialName("content")
    val content: String,
)
