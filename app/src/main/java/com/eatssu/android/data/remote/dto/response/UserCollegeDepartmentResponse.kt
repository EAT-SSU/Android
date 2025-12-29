package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
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

fun UserCollegeDepartmentResponse.toDomain(): Pair<College, Department>? {
    val colId = collegeId ?: return null
    val colName = collegeName ?: return null
    val deptId = departmentId ?: return null
    val deptName = departmentName ?: return null
    return Pair(
        College(collegeId = colId, collegeName = colName),
        Department(departmentId = deptId, departmentName = deptName)
    )
}