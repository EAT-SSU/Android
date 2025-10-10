package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class HealthCheckResponse(
    @SerializedName("status")
    val status: String
)

