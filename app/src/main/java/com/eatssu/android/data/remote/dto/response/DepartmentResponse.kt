package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Department
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DepartmentResponse(
    @SerialName("id")
    val departmentId: Int? = null,
    @SerialName("name")
    val departmentName: String? = null,
)

// 이 함수가 null을 반환하는 경우, 이 함수를 호출하는 UserRepositoryImpl에서 mapNotNull로 걸러짐
fun DepartmentResponse.toDomain(): Department? {
    val id = departmentId ?: return null
    val name = departmentName ?: return null
    return Department(departmentId = id, departmentName = name)
}
