package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.Review
import com.google.gson.annotations.SerializedName

data class MenuReviewListResponse(
    @SerializedName("numberOfElements") val numberOfElements: Int? = null,
    @SerializedName("hasNext") val hasNext: Boolean? = null,
    @SerializedName("dataList") val dataList: ArrayList<DataList> = arrayListOf(),
) {
    data class DataList(
        @SerializedName("reviewId") val reviewId: Long? = null,
        @SerializedName("menu") val menu: Menu? = Menu(),
        @SerializedName("writerId") val writerId: Long? = null,
        @SerializedName("isWriter") val isWriter: Boolean? = null,
        @SerializedName("writerNickname") val writerNickname: String? = null,
        @SerializedName("rating") val rating: Int? = null,
        @SerializedName("writtenAt") val writtenAt: String? = null,
        @SerializedName("content") val content: String? = null,
        @SerializedName("imageUrls") val imageUrls: ArrayList<String> = arrayListOf(),
    ) {
        data class Menu(
            @SerializedName("id") val id: Long? = null,
            @SerializedName("name") val name: String? = null,
            @SerializedName("isLike") val isLike: Boolean? = null
        )
    }
}

fun MenuReviewListResponse?.toDomain(): List<Review> {
    return this?.dataList?.map { data ->
        Review(
            reviewId = data.reviewId ?: -1L,
            isWriter = data.isWriter ?: false,
            menuLikeInfoList = listOf(
                Review.MenuLikeInfo(
                    menuId = data.menu?.id ?: -1L,
                    name = data.menu?.name ?: "",
                    isLike = data.menu?.isLike ?: false
                ),
            ),
            writerNickname = data.writerNickname ?: "",
            rating = data.rating ?: 0,
            writeDate = data.writtenAt ?: "",
            content = data.content ?: "",
            imgUrl = data.imageUrls.firstOrNull(),
        )
    } ?: emptyList()
}