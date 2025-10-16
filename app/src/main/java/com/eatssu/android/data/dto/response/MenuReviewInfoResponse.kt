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
    oneStarCount = reviewRatingCount?.oneStarCount ?: 0,
    twoStarCount = reviewRatingCount?.twoStarCount ?: 0,
    threeStarCount = reviewRatingCount?.threeStarCount ?: 0,
    fourStarCount = reviewRatingCount?.fourStarCount ?: 0,
    fiveStarCount = reviewRatingCount?.fiveStarCount ?: 0,
)
