package com.eatssu.android.data.mapper

import com.eatssu.android.data.dto.response.CollegeResponse
import com.eatssu.android.data.dto.response.DepartmentResponse
import com.eatssu.android.data.dto.response.UserCollegeDepartmentResponse
import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department

fun CollegeResponse.toDomain() = College(
    collegeId = this.collegeId,
    collegeName = this.collegeName,
)

fun DepartmentResponse.toDomain() = Department(
    departmentId = this.departmentId,
    departmentName = this.departmentName,
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
