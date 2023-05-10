package com.eatssu.android.data.model.request

data class ModifyReviewRequest(
    val content: String,
    val grade: Int,
    val reviewTags: List<String>
)