package com.eatssu.android.data.model.request

data class SignInRequest(
    val email: String,
    val nickname: String,
    val pwd: String
)