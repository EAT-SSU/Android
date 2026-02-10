package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Review
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealReviewListResponse(
    @SerialName("numberOfElements") val numberOfElements: Int? = null,
    @SerialName("hasNext") val hasNext: Boolean? = null,
    @SerialName("dataList") val dataList: List<DataList> = arrayListOf()
) {
    @Serializable
    data class DataList(
        @SerialName("reviewId") val reviewId: Long? = null,
        @SerialName("menuList") val menuList: List<MenuList> = arrayListOf(),
        @SerialName("writerId") val writerId: Long? = null,
        @SerialName("isWriter") val isWriter: Boolean? = null,
        @SerialName("writerNickname") val writerNickname: String? = null,
        @SerialName("rating") val rating: Int? = null,
        @SerialName("writtenAt") val writtenAt: String? = null,
        @SerialName("content") val content: String? = null,
        @SerialName("imageUrls") val imageUrls: List<String> = arrayListOf(),
    ) {
        @Serializable
        data class MenuList(
            @SerialName("id") val id: Long? = null,
            @SerialName("name") val name: String? = null,
            @SerialName("isLike") val isLike: Boolean? = null,
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
