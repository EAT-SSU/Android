package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.College
import com.google.gson.annotations.SerializedName

data class CollegeResponse(
    @SerializedName("id")
    val collegeId: Int?,
    @SerializedName("name")
    val collegeName: String?
)

fun CollegeResponse.toDomain() = College(
    collegeId = this.collegeId ?: -1,
    collegeName = this.collegeName ?: "단과대",
)