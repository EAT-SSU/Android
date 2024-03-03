package com.eatssu.android.data.model

data class Review(
    var name: String? = null,
    var reviewCnt: Int? = null,
    var mainRating: Double? = null,
    var amountRating: Double? = null,
    var tasteRating: Double? = null,
    var ratingDetails: RatingDetails,
    var reviewList: List<Reviews>? = null,
)

data class RatingDetails(
    var one: Int,
    var two: Int,
    var three: Int,
    var four: Int,
    var five: Int,
)

data class Reviews(
    val menu: String,
    val writerNickname: String,

    val mainGrade: Int,
    val amountGrade: Int,
    val tasteGrade: Int,

    val writeDate: String,

    val content: String,
    val imgUrlList: List<String>,
)