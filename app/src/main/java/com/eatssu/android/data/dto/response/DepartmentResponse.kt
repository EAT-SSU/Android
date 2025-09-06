package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.Department
import com.google.gson.annotations.SerializedName

data class DepartmentResponse(
    @SerializedName("id")
    val departmentId: Int?,
    @SerializedName("name")
    val departmentName: String?,
)

fun DepartmentResponse.toDomain() = Department(
    departmentId = this.departmentId ?: -1,
    departmentName = this.departmentName ?: "학과",
)