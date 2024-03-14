package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.Review
import com.google.gson.annotations.SerializedName

data class GetReviewListResponse(
    @SerializedName("numberOfElements") var numberOfElements: Int? = null,
    @SerializedName("hasNext") var hasNext: Boolean? = null,
    @SerializedName("dataList") var dataList: ArrayList<DataList>? = arrayListOf(),
) {

    data class DataList(

        @SerializedName("reviewId") var reviewId: Long,
        @SerializedName("menu") var menu: String,
        @SerializedName("writerId") var writerId: Long,
        @SerializedName("isWriter") var isWriter: Boolean,
        @SerializedName("writerNickname") var writerNickname: String,
        @SerializedName("mainRating") var mainRating: Int,
        @SerializedName("amountRating") var amountRating: Int,
        @SerializedName("tasteRating") var tasteRating: Int,
        @SerializedName("writedAt") var writedAt: String,
        @SerializedName("content") var content: String,
        @SerializedName("imageUrls") var imageUrls: ArrayList<String?> = arrayListOf(),

        )
}

fun GetReviewListResponse.toReviewList(): List<Review> {
    return dataList!!.map { data ->
        Review(
            reviewId = data.reviewId,
            isWriter = data.isWriter,
            menu = data.menu,
            writerNickname = data.writerNickname,
            mainGrade = data.mainRating,
            amountGrade = data.amountRating,
            tasteGrade = data.tasteRating,
            writeDate = data.writedAt,
            content = data.content,
            imgUrlList = data.imageUrls[0] ?: ""
        )
    }
}