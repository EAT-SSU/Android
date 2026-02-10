package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserCollegeDepartmentResponse(
    @SerialName("departmentId")
    val departmentId: Int?,
    @SerialName("departmentName")
    val departmentName: String?,
    @SerialName("collegeId")
    val collegeId: Int?,
    @SerialName("collegeName")
    val collegeName: String?,
)

// 이 함수가 null을 반환하는 경우, 이 함수를 호출하는 UserRepositoryImpl에서 mapNotNull로 걸러짐
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
