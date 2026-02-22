package com.eatssu.android.data.remote.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UserDepartmentRequest(
    val departmentId: Int
)
