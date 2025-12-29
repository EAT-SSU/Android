package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.College
import com.google.gson.annotations.SerializedName

data class CollegeResponse(
    @SerializedName("id")
    val collegeId: Int?,
    @SerializedName("name")
    val collegeName: String?
)

fun CollegeResponse.toDomain(): College? {
    val id = collegeId ?: return null
    val name = collegeName ?: return null
    return College(collegeId = id, collegeName = name)
}