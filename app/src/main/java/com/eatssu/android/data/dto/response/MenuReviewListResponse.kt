package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.Review
import com.google.gson.annotations.SerializedName

data class MenuReviewListResponse(
    @SerializedName("numberOfElements") var numberOfElements: Int? = null,
    @SerializedName("hasNext") var hasNext: Boolean? = null,
    @SerializedName("dataList") var dataList: ArrayList<DataList> = arrayListOf()
) {
    data class DataList( //todo 변경
        @SerializedName("reviewId") var reviewId: Long? = null,
        @SerializedName("menu") var menu: String? = null,
        @SerializedName("writerId") var writerId: Long? = null,
        @SerializedName("isWriter") var isWriter: Boolean? = null,
        @SerializedName("writerNickname") var writerNickname: String? = null,
        @SerializedName("mainRating") var mainRating: Int? = null,
        @SerializedName("amountRating") var amountRating: String? = null,
        @SerializedName("tasteRating") var tasteRating: String? = null,
        @SerializedName("writedAt") var writtenAt: String? = null,
        @SerializedName("content") var content: String? = null,
        @SerializedName("imageUrls") var imageUrls: ArrayList<String> = arrayListOf()
    )
}

fun MenuReviewListResponse.toDomain(): List<Review> {
    return this.dataList.map { data ->
        Review(
            reviewId = data.reviewId ?: 0,
            isWriter = data.isWriter ?: false,
            menuList = listOf(
                Review.Menu(
                    0,
                    data.menu ?: "",
                    false
                ),
            ),
            writerNickname = data.writerNickname ?: "유저",
            mainGrade = data.mainRating ?: 0,
            writeDate = data.writtenAt ?: "",
            content = data.content ?: "",
            imgUrl = data.imageUrls.firstOrNull(),
        )
    } ?: emptyList()
}