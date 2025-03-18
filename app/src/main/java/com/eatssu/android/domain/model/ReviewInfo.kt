package com.eatssu.android.domain.model

data class ReviewInfo(
    var name: String,
    var reviewCnt: Int,
    var mainRating: Double,
    var amountRating: Double,
    var tasteRating: Double,
    var one: Int,
    var two: Int,
    var three: Int,
    var four: Int,
    var five: Int,
)