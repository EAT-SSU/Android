package com.eatssu.android.data.model.request

import com.eatssu.android.view.review.Review

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