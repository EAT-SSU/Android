package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.ReviewInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.round

@Serializable
data class MealReviewInfoResponse(
    @SerialName("menuNames") val menuNames: List<String>? = null,
    @SerialName("totalReviewCount") val totalReviewCount: Int? = null,
    @SerialName("rating") val rating: Double? = null,
    @SerialName("likeCount") val likeCount: Int? = null,
    @SerialName("reviewRatingCount") val reviewRatingCount: ReviewRatingCount? = ReviewRatingCount(),
) {
    @Serializable
    data class ReviewRatingCount(
        @SerialName("oneStarCount") val oneStarCount: Int? = null,
        @SerialName("twoStarCount") val twoStarCount: Int? = null,
        @SerialName("threeStarCount") val threeStarCount: Int? = null,
        @SerialName("fourStarCount") val fourStarCount: Int? = null,
        @SerialName("fiveStarCount") val fiveStarCount: Int? = null,
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
