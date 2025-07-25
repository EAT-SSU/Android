package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class UserDepartmentResponse(
    @SerializedName("departmentName")
    val departmentName: String?,
)