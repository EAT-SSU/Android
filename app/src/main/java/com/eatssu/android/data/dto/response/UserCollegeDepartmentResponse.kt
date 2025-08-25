package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class UserCollegeDepartmentResponse(
    @SerializedName("departmentId")
    val departmentId: Int?,
    @SerializedName("departmentName")
    val departmentName: String?,
    @SerializedName("collegeId")
    val collegeId: Int?,
    @SerializedName("collegeName")
    val collegeName: String?,
)