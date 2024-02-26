package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetReportTypeResponseDto(
    @SerializedName("reportType")
    val reportType: String,

    @SerializedName("typeDescription")
    val typeDescription: String
)
