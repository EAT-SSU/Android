package com.eatssu.android.data.model.response

data class BaseResponse<T>(
    val isSuccess: Boolean,
    val code: Int,
    val message: String,
    val result: T? = null
)