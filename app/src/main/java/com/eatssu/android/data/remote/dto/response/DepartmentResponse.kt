package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Department
import com.google.gson.annotations.SerializedName

data class DepartmentResponse(
    @SerializedName("id")
    val departmentId: Int?,
    @SerializedName("name")
    val departmentName: String?,
)

fun DepartmentResponse.toDomain(): Department? {
    val id = departmentId ?: return null
    val name = departmentName ?: return null
    return Department(departmentId = id, departmentName = name)
}