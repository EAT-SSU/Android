package com.eatssu.android.data.remote.dto.request

import com.eatssu.common.enums.DeviceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginWithKakaoRequest(
    @SerialName("email")
    val email: String,

    @SerialName("providerId")
    val providerId: String,

    @SerialName("deviceType")
    val deviceType: DeviceType,
)
