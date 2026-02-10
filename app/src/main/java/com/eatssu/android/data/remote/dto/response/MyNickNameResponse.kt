package com.eatssu.android.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyNickNameResponse(
    @SerialName("nickname") var nickname: String? = null,
    @SerialName("provider") var provider: String? = null,
)
