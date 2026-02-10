package com.eatssu.android.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("isSuccess") var isSuccess: Boolean? = null,
    @SerialName("code") var code: Int? = null,
    @SerialName("message") var message: String? = null,
    @SerialName("result") var result: T? = null,
)
