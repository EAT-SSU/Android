package com.eatssu.android.data.dto.request

data class ModifyReviewRequest(
    val content: String,
    val grade: Int,
    val reviewTags: List<String>,
)