package com.eatssu.android.base

data class BaseResponse<T>(
    val code: Int,
    val isSuccess: Boolean,
    val message: String,
    val result: T? = null
)