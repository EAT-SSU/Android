package com.eatssu.android.data.model.request

import com.google.gson.annotations.SerializedName

data class LoginWithKakaoRequestDto (
    @SerializedName("email")
    val email: String,

    @SerializedName("providerId")
    val providerId: String
)