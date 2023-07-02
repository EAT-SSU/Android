package com.eatssu.android.data.model.request

import com.eatssu.android.ui.review.Review

data class WriteReviewDetailRequest(
    val multipartFileList: List<String>?,
    val reviewCreate: Review.ReviewCreate
){
    data class ReviewCreate(
        val content: String?,
        val grade: Int,
        val reviewTags: List<String>?
    )
}