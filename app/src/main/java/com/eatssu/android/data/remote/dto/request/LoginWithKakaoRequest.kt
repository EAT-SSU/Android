package com.eatssu.android.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class LoginWithKakaoRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("providerId")
    val providerId: String,
)