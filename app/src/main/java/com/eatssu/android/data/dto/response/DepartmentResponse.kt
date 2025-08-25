package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class DepartmentResponse(
    @SerializedName("id")
    val departmentId: Int,
    @SerializedName("name")
    val departmentName: String,
)
