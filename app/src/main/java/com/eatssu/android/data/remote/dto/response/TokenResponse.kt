package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("accessToken")
    val accessToken: String,

    @SerialName("refreshToken")
    val refreshToken: String,
)

fun TokenResponse.toDomain() = Token(
    accessToken = accessToken,
    refreshToken = refreshToken,
)
