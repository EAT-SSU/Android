package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetMyInfoResponseDto(

    @SerializedName("nickname") var nickname: String? = null,
    @SerializedName("provider") var provider: String? = null,
)
