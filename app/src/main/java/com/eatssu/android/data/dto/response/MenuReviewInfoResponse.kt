package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.ReviewInfo
import com.google.gson.annotations.SerializedName

data class MenuReviewInfoResponse(
    @SerializedName("menuName") var menuName: String? = null,
    @SerializedName("totalReviewCount") var totalReviewCount: Int? = null,
    @SerializedName("rating") var rating: Double? = null,
    @SerializedName("likeCount") var likeCount: Int? = null,
    @SerializedName("reviewRatingCount") var reviewRatingCount: ReviewRatingCount? = ReviewRatingCount(),
) {
    data class ReviewRatingCount(
        @SerializedName("oneStarCount") var oneStarCount: Int? = null,
        @SerializedName("twoStarCount") var twoStarCount: Int? = null,
        @SerializedName("threeStarCount") var threeStarCount: Int? = null,
        @SerializedName("fourStarCount") var fourStarCount: Int? = null,
        @SerializedName("fiveStarCount") var fiveStarCount: Int? = null,
    )
}

fun MenuReviewInfoResponse.toDomain() = ReviewInfo(
    reviewCnt = totalReviewCount ?: 0,
    rating = rating ?: 0.0,
    one = reviewRatingCount?.oneStarCount ?: 0,
    two = reviewRatingCount?.twoStarCount ?: 0,
    three = reviewRatingCount?.threeStarCount ?: 0,
    four = reviewRatingCount?.fourStarCount ?: 0,
    five = reviewRatingCount?.fiveStarCount ?: 0,
)
