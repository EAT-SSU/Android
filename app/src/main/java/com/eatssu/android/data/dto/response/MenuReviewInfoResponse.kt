package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.ReviewInfo
import com.google.gson.annotations.SerializedName

data class GetMenuReviewInfoResponse(

    @SerializedName("menuName") var menuName: String,
    @SerializedName("totalReviewCount") var totalReviewCount: Int,
    @SerializedName("mainRating") var mainRating: Double? = null,
    @SerializedName("amountRating") var amountRating: Double? = null,
    @SerializedName("tasteRating") var tasteRating: Double? = null,
    @SerializedName("reviewRatingCount") var reviewRatingCount: ReviewRatingCount,
) {
    data class ReviewRatingCount(

        @SerializedName("oneStarCount") var oneStarCount: Int,
        @SerializedName("twoStarCount") var twoStarCount: Int,
        @SerializedName("threeStarCount") var threeStarCount: Int,
        @SerializedName("fourStarCount") var fourStarCount: Int,
        @SerializedName("fiveStarCount") var fiveStarCount: Int,

        )

}

fun GetMenuReviewInfoResponse.asReviewInfo() = ReviewInfo(

    name = menuName,
    reviewCnt = totalReviewCount,
    mainRating = mainRating ?: 0.0,
    amountRating = amountRating ?: 0.0,
    tasteRating = tasteRating ?: 0.0,
    one = reviewRatingCount.oneStarCount,
    two = reviewRatingCount.twoStarCount,
    three = reviewRatingCount.threeStarCount,
    four = reviewRatingCount.fourStarCount,
    five = reviewRatingCount.fiveStarCount,

    )
