package com.eatssu.android.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String,
)

fun TokenResponse.toDomain() = com.eatssu.android.domain.model.Token(
    accessToken = accessToken,
    refreshToken = refreshToken,
)
