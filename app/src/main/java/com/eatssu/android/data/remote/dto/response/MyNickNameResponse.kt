package com.eatssu.android.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyNickNameResponse(
    @SerialName("nickname") val nickname: String? = null,
    @SerialName("provider") val provider: String? = null,
)
