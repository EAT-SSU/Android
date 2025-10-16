package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("url") val url: String? = null,
)