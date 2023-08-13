package com.eatssu.android.data.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("email")
    val email: String,

    @SerializedName("pwd")
    val pwd: String
)