package com.eatssu.android.data.model.request

data class GetReviewDetailRequest(
    val multipartFileList: List<String>,
    val reviewCreate: ReviewCreate
){
    data class ReviewCreate(
        val content: String,
        val grade: Int,
        val reviewTags: List<String>
    )
}