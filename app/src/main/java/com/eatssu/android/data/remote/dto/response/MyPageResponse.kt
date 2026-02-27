package com.eatssu.android.data.remote.dto.response

import com.eatssu.common.enums.Provider
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyPageResponse(
    @SerialName("nickname") val nickname: String? = null,
    @SerialName("provider") val provider: Provider? = null,
    @SerialName("departmentId") val departmentId: Long? = null,
    @SerialName("departmentName") val departmentName: String? = null,
    @SerialName("collegeId") val collegeId: Long? = null,
    @SerialName("collegeName") val collegeName: String? = null,
)
