package com.eatssu.android.data.model.response

data class BaseResponse(
    val error: String,
    val path: String,
    val status: Int,
    val timestamp: String
)