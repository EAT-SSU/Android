package com.eatssu.android.ui.review

import com.eatssu.android.data.model.request.WriteReviewDetailRequest

data class Review(
    val multipartFileList: List<String>,
    val reviewCreate: WriteReviewDetailRequest.ReviewCreate
) {
    data class ReviewCreate(
        val content: String,
        val grade: Int,
        val reviewTags: List<String>
    )
}