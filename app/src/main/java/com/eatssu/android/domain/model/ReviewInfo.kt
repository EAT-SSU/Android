package com.eatssu.android.domain.model

data class ReviewInfo(
    var name: String,
    var reviewCnt: Int,
    var mainRating: Double,
    var likeCount: Int,
    var unlikeCount: Int,
    var one: Int,
    var two: Int,
    var three: Int,
    var four: Int,
    var five: Int,
)