package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Review
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuReviewListResponse(
    @SerialName("numberOfElements") val numberOfElements: Int? = null,
    @SerialName("hasNext") val hasNext: Boolean? = null,
    @SerialName("dataList") val dataList: List<DataList> = arrayListOf(),
) {
    @Serializable
    data class DataList(
        @SerialName("reviewId") val reviewId: Long? = null,
        @SerialName("menu") val menu: Menu? = Menu(),
        @SerialName("writerId") val writerId: Long? = null,
        @SerialName("isWriter") val isWriter: Boolean? = null,
        @SerialName("writerNickname") val writerNickname: String? = null,
        @SerialName("rating") val rating: Int? = null,
        @SerialName("writtenAt") val writtenAt: String? = null,
        @SerialName("content") val content: String? = null,
        @SerialName("imageUrls") val imageUrls: List<String> = arrayListOf(),
    ) {
        @Serializable
        data class Menu(
            @SerialName("id") val id: Long? = null,
            @SerialName("name") val name: String? = null,
            @SerialName("isLike") val isLike: Boolean? = null
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
