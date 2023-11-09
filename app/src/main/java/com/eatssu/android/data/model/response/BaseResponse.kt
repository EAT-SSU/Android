package com.eatssu.android.data.model.response

data class BaseResponse(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: Result
)