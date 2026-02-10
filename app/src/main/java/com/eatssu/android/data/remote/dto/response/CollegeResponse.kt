package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.College
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollegeResponse(
    @SerialName("id")
    val collegeId: Int?,
    @SerialName("name")
    val collegeName: String?
)

// 이 함수가 null을 반환하는 경우, 이 함수를 호출하는 UserRepositoryImpl에서 mapNotNull로 걸러짐
fun CollegeResponse.toDomain(): College? {
    val id = collegeId ?: return null
    val name = collegeName ?: return null
    return College(collegeId = id, collegeName = name)
}
