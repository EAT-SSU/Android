package com.eatssu.android.data.model.request

import com.google.gson.annotations.SerializedName

data class SignUpRequestDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("pwd")
    val pwd: String
)