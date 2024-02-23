package com.eatssu.android.data.dto.request

import com.eatssu.android.ui.review.write.Review

data class WriteReviewDetailRequestDto(
    val multipartFileList: List<String>,
    val reviewCreate: Review.ReviewCreate
){
    data class ReviewCreate(
        val content: String,
        val mainGrade: Int,
        val amountGrade: Int,
        val tasteGrade: Int
        )
}