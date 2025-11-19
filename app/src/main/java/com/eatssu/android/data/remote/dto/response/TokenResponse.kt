package com.eatssu.android.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,
)

fun TokenResponse.toDomain() = com.eatssu.android.domain.model.Token(
    accessToken = accessToken,
    refreshToken = refreshToken,
)