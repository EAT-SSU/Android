package com.eatssu.android.domain.model

data class Review(
    val isWriter: Boolean,
    val reviewId: Long,

    val menu: String,
    val writerNickname: String,

    val mainGrade: Int,
    val amountGrade: Int,
    val tasteGrade: Int,

    val writeDate: String,

    val content: String,
    val imgUrl: ArrayList<String>?,
)