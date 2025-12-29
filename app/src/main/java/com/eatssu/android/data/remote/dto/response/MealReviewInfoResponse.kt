package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.ReviewInfo
import com.google.gson.annotations.SerializedName
import kotlin.math.round

data class MealReviewInfoResponse(
    @SerializedName("menuNames") val menuNames: List<String>? = null,
    @SerializedName("totalReviewCount") val totalReviewCount: Int? = null,
    @SerializedName("rating") val rating: Double? = null,
    @SerializedName("likeCount") val likeCount: Int? = null,
    @SerializedName("reviewRatingCount") val reviewRatingCount: ReviewRatingCount? = ReviewRatingCount(),
) {
    data class ReviewRatingCount(
        @SerializedName("oneStarCount") val oneStarCount: Int? = null,
        @SerializedName("twoStarCount") val twoStarCount: Int? = null,
        @SerializedName("threeStarCount") val threeStarCount: Int? = null,
        @SerializedName("fourStarCount") val fourStarCount: Int? = null,
        @SerializedName("fiveStarCount") val fiveStarCount: Int? = null,
    )
}

fun MealReviewInfoResponse.toDomain() = ReviewInfo(
    reviewCnt = totalReviewCount ?: 0,
    rating = (round((rating ?: 0.0) * 10) / 10),
    oneStarCount = reviewRatingCount?.oneStarCount ?: 0,
    twoStarCount = reviewRatingCount?.twoStarCount ?: 0,
    threeStarCount = reviewRatingCount?.threeStarCount ?: 0,
    fourStarCount = reviewRatingCount?.fourStarCount ?: 0,
    fiveStarCount = reviewRatingCount?.fiveStarCount ?: 0,
)
