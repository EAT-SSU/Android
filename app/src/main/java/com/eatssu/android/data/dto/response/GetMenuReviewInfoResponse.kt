package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.ReviewInfo
import com.google.gson.annotations.SerializedName

data class GetMenuReviewInfoResponse(

    @SerializedName("menuName") var menuName: String? = null,
    @SerializedName("totalReviewCount") var totalReviewCount: Int? = null,
    @SerializedName("mainRating") var mainRating: Double? = null,
    @SerializedName("amountRating") var amountRating: Double? = null,
    @SerializedName("tasteRating") var tasteRating: Double? = null,
    @SerializedName("reviewRatingCount") var reviewRatingCount: ReviewRatingCount = ReviewRatingCount(),
) {
    data class ReviewRatingCount(

        @SerializedName("oneStarCount") var oneStarCount: Int? = null,
        @SerializedName("twoStarCount") var twoStarCount: Int? = null,
        @SerializedName("threeStarCount") var threeStarCount: Int? = null,
        @SerializedName("fourStarCount") var fourStarCount: Int? = null,
        @SerializedName("fiveStarCount") var fiveStarCount: Int? = null,

        )

}

fun GetMenuReviewInfoResponse.asReviewInfo() = ReviewInfo(

    name = menuName ?: "",
    reviewCnt = totalReviewCount ?: 0,
    mainRating = mainRating ?: 0.0,
    amountRating = amountRating ?: 0.0,
    tasteRating = tasteRating ?: 0.0,
    one = reviewRatingCount.oneStarCount ?: 0,
    two = reviewRatingCount.twoStarCount ?: 0,
    three = reviewRatingCount.threeStarCount ?: 0,
    four = reviewRatingCount.fourStarCount ?: 0,
    five = reviewRatingCount.fiveStarCount ?: 0,
)
