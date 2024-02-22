package com.eatssu.android.base

import com.eatssu.android.data.dto.response.TokenResponseDto
import kotlinx.serialization.SerialName
import retrofit2.Call

data class BaseResponse<T>(
    @SerialName("isSuccess") val isSuccess: Boolean,
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
    @SerialName("result") val result: T? = null
)