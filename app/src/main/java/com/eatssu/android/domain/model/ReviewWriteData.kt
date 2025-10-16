package com.eatssu.android.domain.model

data class ReviewWriteData(
    val rating: Int,
    val content: String?,
    val likeMenuIdList: List<Long>?,
    val imageUrl: String? = null
)
