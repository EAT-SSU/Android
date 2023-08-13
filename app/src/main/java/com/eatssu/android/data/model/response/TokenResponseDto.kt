package com.eatssu.android.data.model.response

import com.google.gson.annotations.SerializedName

data class TokenResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String
)