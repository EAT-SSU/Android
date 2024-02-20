package com.eatssu.android.base

import kotlinx.serialization.SerialName

data class BaseResponse<T>(
    @SerialName("isSuccess") val isSuccess: Boolean,
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
    @SerialName("result") val result: T? = null
)