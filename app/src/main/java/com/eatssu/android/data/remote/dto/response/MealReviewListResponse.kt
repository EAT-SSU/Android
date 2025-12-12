package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Review
import com.google.gson.annotations.SerializedName

data class MealReviewListResponse(
    @SerializedName("numberOfElements") val numberOfElements: Int? = null,
    @SerializedName("hasNext") val hasNext: Boolean? = null,
    @SerializedName("dataList") val dataList: List<DataList> = arrayListOf()
) {
    data class DataList(
        @SerializedName("reviewId") val reviewId: Long? = null,
        @SerializedName("menuList") val menuList: List<MenuList> = arrayListOf(),
        @SerializedName("writerId") val writerId: Long? = null,
        @SerializedName("isWriter") val isWriter: Boolean? = null,
        @SerializedName("writerNickname") val writerNickname: String? = null,
        @SerializedName("rating") val rating: Int? = null,
        @SerializedName("writtenAt") val writtenAt: String? = null,
        @SerializedName("content") val content: String? = null,
        @SerializedName("imageUrls") val imageUrls: List<String> = arrayListOf(),
    ) {
        data class MenuList(
            @SerializedName("id") val id: Long? = null,
            @SerializedName("name") val name: String? = null,
            @SerializedName("isLike") val isLike: Boolean? = null,
        )
    }
}


fun MealReviewListResponse?.toDomain(): List<Review> {
    // MealReviewListResponse 객체 자체가 null이면 emptyList() 반환
    return this?.dataList?.map { data ->
        Review(
            reviewId = data.reviewId ?: -1L,
            isWriter = data.isWriter ?: false,
            menuLikeInfoList = data.menuList.map { menu ->
                Review.MenuLikeInfo(
                    menuId = menu.id ?: -1L,
                    name = menu.name ?: "",
                    isLike = menu.isLike ?: false
                )
            },
            writerNickname = data.writerNickname ?: "",
            rating = data.rating ?: 0,
            writeDate = data.writtenAt ?: "",
            content = data.content ?: "",
            imgUrl = data.imageUrls.firstOrNull(),
        )
    } ?: emptyList()
}