package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.Review
import com.google.gson.annotations.SerializedName

data class MyReviewResponse(
    @SerializedName("numberOfElements") var numberOfElements: Int? = null,
    @SerializedName("hasNext") var hasNext: Boolean? = null,
    @SerializedName("dataList") var dataList: ArrayList<DataList> = arrayListOf()
) {
    data class DataList(

        @SerializedName("reviewId") var reviewId: Long? = null,
        @SerializedName("rating") var rating: Int? = null,
        @SerializedName("writtenAt") var writtenAt: String? = null,
        @SerializedName("content") var content: String? = null,
        @SerializedName("imageUrls") var imageUrls: ArrayList<String> = arrayListOf(),
        @SerializedName("likedMenuNames") var likedMenuNames: ArrayList<String> = arrayListOf(),
        @SerializedName("menuNames") var menuNames: String? = null
        )
}

fun MyReviewResponse.toDomain(): List<Review> {
    return dataList.map { data ->
        Review(
            reviewId = data.reviewId ?: 0,
            isWriter = true,
            menu = data.menuNames ?: "",
            writerNickname = "",
            mainGrade = data.rating ?: 0,
            writeDate = data.writtenAt ?: "",
            content = data.content ?: "",
            imgUrl = data.imageUrls.firstOrNull(),
            likeMenuList = data.likedMenuNames
        )
    }
}