package com.eatssu.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class LoginWithKakaoRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("providerId")
    val providerId: String,
)