package com.eatssu.android.data.model

data class Review(
    val multipartFileList: List<String>,
    val reviewCreate: ReviewCreate,
)

data class ReviewCreate(
    val content: String,
    val mainGrade: Int,
    val amountGrade: Int,
    val tasteGrade: Int,
)