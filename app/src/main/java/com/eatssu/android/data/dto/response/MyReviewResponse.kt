package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class MyReviewResponse(
    @SerializedName("numberOfElements") var numberOfElements: Int? = null,
    @SerializedName("hasNext") var hasNext: Boolean? = null,
    @SerializedName("dataList") var dataList: List<DataList>,

    ) {
    data class DataList(
        @SerializedName("reviewId") var reviewId: Int,
        @SerializedName("mainRating") var mainRating: Int,
        @SerializedName("amountRating") var amountRating: Int,
        @SerializedName("tasteRating") var tasteRating: Int,
        @SerializedName("writeDate") var writeDate: String,
        @SerializedName("menuName") var menuName: String,
        @SerializedName("content") var content: String,
        @SerializedName("imgUrlList") var imgUrlList: List<String>,
    )
}