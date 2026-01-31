package com.eatssu.android.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class MyNickNameResponse(
    @SerializedName("nickname") var nickname: String? = null,
    @SerializedName("provider") var provider: String,
)
