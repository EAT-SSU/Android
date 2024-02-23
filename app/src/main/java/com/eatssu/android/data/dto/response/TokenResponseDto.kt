package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class TokenResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String
)