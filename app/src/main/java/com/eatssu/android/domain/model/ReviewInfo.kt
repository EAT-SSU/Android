package com.eatssu.android.domain.model

data class ReviewInfo(
    var reviewCnt: Int,
    var rating: Double,
    var oneStarCount: Int,
    var twoStarCount: Int,
    var threeStarCount: Int,
    var fourStarCount: Int,
    var fiveStarCount: Int,
)