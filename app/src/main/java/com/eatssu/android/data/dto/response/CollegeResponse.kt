package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class CollegeResponse(
    @SerializedName("id")
    val collegeId: Int,
    @SerializedName("name")
    val collegeName: String
)
