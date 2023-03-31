package com.eatssu.android.data.model.request

data class TokenRequest(
    val accessToken: String,
    val refreshToken: String
)