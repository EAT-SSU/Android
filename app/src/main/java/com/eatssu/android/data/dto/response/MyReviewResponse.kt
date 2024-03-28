package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.Review
import com.google.gson.annotations.SerializedName

data class MyReviewResponse(
    @SerializedName("numberOfElements") var numberOfElements: Int? = null,
    @SerializedName("hasNext") var hasNext: Boolean? = null,
    @SerializedName("dataList") var dataList: List<DataList>,

    ) {
    data class DataList(
        @SerializedName("reviewId") var reviewId: Long,
        @SerializedName("mainRating") var mainRating: Int,
        @SerializedName("amountRating") var amountRating: Int,
        @SerializedName("tasteRating") var tasteRating: Int,
        @SerializedName("writeDate") var writeDate: String,
        @SerializedName("menuName") var menuName: String,
        @SerializedName("content") var content: String,
        @SerializedName("imgUrlList") var imgUrlList: ArrayList<String>? = arrayListOf(),

        )
}

fun MyReviewResponse.toReviewList(): List<Review> {
    return dataList.map { data ->
        Review(
            reviewId = data.reviewId,
            isWriter = true,
            menu = data.menuName,
            writerNickname = "",
            mainGrade = data.mainRating,
            amountGrade = data.amountRating,
            tasteGrade = data.tasteRating,
            writeDate = data.writeDate,
            content = data.content,
            imgUrl = data.imgUrlList
        )
    }
}