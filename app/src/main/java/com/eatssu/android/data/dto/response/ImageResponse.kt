package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("url") var url: String? = null,
)