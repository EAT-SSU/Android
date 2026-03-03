package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Review
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyReviewListResponse(
    @SerialName("numberOfElements") val numberOfElements: Int? = null,
    @SerialName("hasNext") val hasNext: Boolean? = null,
    @SerialName("dataList") val dataList: List<DataList>? = listOf()
) {
    @Serializable
    data class DataList(

        @SerialName("reviewId") val reviewId: Long? = null,
        @SerialName("rating") val rating: Int? = null,
        @SerialName("writtenAt") val writtenAt: String? = null,
        @SerialName("content") val content: String? = null,
        @SerialName("imageUrls") val imageUrls: List<String?> = listOf(),
        @SerialName("menuList") val menuList: List<MenuList?> = listOf()
    ) {
        @Serializable
        data class MenuList(
            @SerialName("id") val id: Long? = null,
            @SerialName("name") val name: String? = null,
            @SerialName("isLike") val isLike: Boolean? = null
        )
    }
}

fun MyReviewListResponse?.toDomain(): List<Review> {
    return this?.dataList?.map { data ->
        Review(
            reviewId = data.reviewId ?: -1L,
            isWriter = true,
            menuLikeInfoList = data.menuList.filterNotNull().map { menu ->
                Review.MenuLikeInfo(
                    menuId = menu.id ?: -1L,
                    name = menu.name ?: "",
                    isLike = menu.isLike ?: false
                )
            },
            writerNickname = "",
            rating = data.rating ?: 0,
            writeDate = data.writtenAt ?: "",
            content = data.content ?: "",
            imgUrl = data.imageUrls.firstOrNull { it != null },
        )
    } ?: emptyList()
}