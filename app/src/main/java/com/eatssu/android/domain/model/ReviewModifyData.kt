package com.eatssu.android.domain.model

data class ReviewModifyData(
    val rating: Int,
    val content: String,
    val menuLikes: List<Review.Menu>,
)
