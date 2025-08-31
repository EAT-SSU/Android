package com.eatssu.android.domain.model

data class ReviewWriteData(
    val rating: Int,
    val content: String,
    val menuLikes: List<Long>,
    val imageUrl: String? = null
)
