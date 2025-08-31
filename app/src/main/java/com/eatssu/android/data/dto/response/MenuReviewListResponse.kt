package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.Review
import com.google.gson.annotations.SerializedName

data class MenuReviewListResponse(
    @SerializedName("numberOfElements") var numberOfElements: Int? = null,
    @SerializedName("hasNext") var hasNext: Boolean? = null,
    @SerializedName("dataList") var dataList: ArrayList<DataList> = arrayListOf()
) {
    data class DataList(
        @SerializedName("reviewId") var reviewId: Long? = null,
        @SerializedName("writerId") var writerId: Long? = null,
        @SerializedName("isWriter") var isWriter: Boolean? = null,
        @SerializedName("writerNickname") var writerNickname: String? = null,
        @SerializedName("rating") var rating: Int? = null,
        @SerializedName("writtenAt") var writtenAt: String? = null,
        @SerializedName("content") var content: String? = null,
        @SerializedName("imageUrls") var imageUrls: ArrayList<String> = arrayListOf(),
        @SerializedName("likedMenuNames") var likedMenuNames: ArrayList<String> = arrayListOf(),
        @SerializedName("menu") var menu: String? = null
    )
}

fun MenuReviewListResponse.toDomain(): List<Review> {
    // MealReviewListResponse 객체 자체가 null이면 emptyList() 반환
    return this.dataList.map { data ->
        Review(
            reviewId = data.reviewId ?: 0,
            isWriter = data.isWriter ?: false,
            menu = data.menu ?: "",
            writerNickname = data.writerNickname ?: "유저",
            mainGrade = data.rating ?: 0,
            writeDate = data.writtenAt ?: "",
            content = data.content ?: "",
            imgUrl = data.imageUrls.firstOrNull(),
            likeMenuList = data.likedMenuNames ?: emptyList()
        )
    } ?: emptyList()
}