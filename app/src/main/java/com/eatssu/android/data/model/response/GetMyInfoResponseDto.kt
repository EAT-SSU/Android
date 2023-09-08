package com.eatssu.android.data.model.response

import com.google.gson.annotations.SerializedName

data class GetMyInfoResponseDto(
    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("accountFrom")
    val accountFrom: String
)
