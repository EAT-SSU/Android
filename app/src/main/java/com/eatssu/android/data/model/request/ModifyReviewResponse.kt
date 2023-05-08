package com.eatssu.android.data.model.request

data class ModifyReviewResponse(
    val content: String,
    val grade: Int,
    val reviewTags: List<String>
)