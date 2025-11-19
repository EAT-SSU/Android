package com.eatssu.android.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("url") val url: String? = null,
)