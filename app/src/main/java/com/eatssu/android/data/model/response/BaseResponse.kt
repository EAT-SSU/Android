package com.eatssu.android.data.model.response

data class BaseResponse<T>(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: T? = null
)