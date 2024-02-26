package com.eatssu.android.base

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("isSuccess") var isSuccess: Boolean? = null,
    @SerializedName("code") var code: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("result") var result: T? = null,
)