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

fun UserCollegeDepartmentResponse.toDomain(): Pair<College, Department> =
    Pair(
        College(
            collegeId = this.collegeId ?: -1,
            collegeName = this.collegeName ?: "단과대"
        ),
        Department(
            departmentId = this.departmentId ?: -1,
            departmentName = this.departmentName ?: "학과"
        )
    )