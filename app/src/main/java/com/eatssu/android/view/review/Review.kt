package com.eatssu.android.view.review

import com.eatssu.android.data.model.request.WriteReviewDetailRequestDto

data class Review(
    val multipartFileList: List<String>,
    val reviewCreate: WriteReviewDetailRequestDto.ReviewCreate
) {
    data class ReviewCreate(
        val content: String?,
        val grade: Int,
        val reviewTags: List<String>
    )
}