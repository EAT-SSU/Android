package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.ReviewInfo

data class GetMenuReviewInfoResponse(
    val menuName: String,
    val totalReviewCount: Int,
    val mainRating: Double,
    val likeCount: Int,
    val unlikeCount: Int,
    val reviewRatingCount: ReviewRatingCount,
) {
    data class ReviewRatingCount(
        val oneStarCount: Int? = null,
        val twoStarCount: Int? = null,
        val threeStarCount: Int? = null,
        val fourStarCount: Int? = null,
        val fiveStarCount: Int? = null,
    )
}

fun GetMenuReviewInfoResponse.asReviewInfo() = ReviewInfo(

    name = menuName,
    reviewCnt = totalReviewCount,
    mainRating = mainRating,
    likeCount = likeCount,
    unlikeCount = unlikeCount,
    one = reviewRatingCount.oneStarCount ?: 0,
    two = reviewRatingCount.twoStarCount ?: 0,
    three = reviewRatingCount.threeStarCount ?: 0,
    four = reviewRatingCount.fourStarCount ?: 0,
    five = reviewRatingCount.fiveStarCount ?: 0,
)
