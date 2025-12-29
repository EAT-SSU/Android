package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Department
import com.google.gson.annotations.SerializedName

data class DepartmentResponse(
    @SerializedName("id")
    val departmentId: Int?,
    @SerializedName("name")
    val departmentName: String?,
)

// 이 함수가 null을 반환하는 경우, 이 함수를 호출하는 UserRepositoryImpl에서 mapNotNull로 걸러짐
fun DepartmentResponse.toDomain(): Department? {
    val id = departmentId ?: return null
    val name = departmentName ?: return null
    return Department(departmentId = id, departmentName = name)
}