package com.eatssu.android.data.model.request

import com.eatssu.android.ui.review.Review

data class WriteReviewDetailRequest(
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