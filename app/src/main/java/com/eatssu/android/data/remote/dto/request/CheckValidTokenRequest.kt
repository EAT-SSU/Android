package com.eatssu.android.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CheckValidTokenRequest(
    val token: String,
)
