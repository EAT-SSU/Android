package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetMealReviewInfoResponse(

    @SerializedName("menuNames") var menuNames: ArrayList<String> = arrayListOf(),
    @SerializedName("totalReviewCount") var totalReviewCount: Int? = null,
    @SerializedName("mainRating") var mainRating: String? = null,
    @SerializedName("amountRating") var amountRating: String? = null,
    @SerializedName("tasteRating") var tasteRating: String? = null,
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

