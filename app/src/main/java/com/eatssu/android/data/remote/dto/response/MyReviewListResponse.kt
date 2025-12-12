package com.eatssu.android.data.remote.dto.response

import com.eatssu.android.domain.model.Review
import com.google.gson.annotations.SerializedName

data class MyReviewListResponse(
    @SerializedName("numberOfElements") val numberOfElements: Int? = null,
    @SerializedName("hasNext") val hasNext: Boolean? = null,
    @SerializedName("dataList") val dataList: ArrayList<DataList>? = arrayListOf()
) {
    data class DataList(

        @SerializedName("reviewId") val reviewId: Long? = null,
        @SerializedName("rating") val rating: Int? = null,
        @SerializedName("writtenAt") val writtenAt: String? = null,
        @SerializedName("content") val content: String? = null,
        @SerializedName("imageUrls") val imageUrls: ArrayList<String> = arrayListOf(),
        @SerializedName("menuList") val menuList: ArrayList<MenuList> = arrayListOf()
    ) {
        data class MenuList(
            @SerializedName("id") val id: Long? = null,
            @SerializedName("name") val name: String? = null,
            @SerializedName("isLike") val isLike: Boolean? = null
        )
    }
}

fun MyReviewListResponse?.toDomain(): List<Review> {
    return this?.dataList?.map { data ->
        Review(
            reviewId = data.reviewId ?: -1L,
            isWriter = true,
            menuLikeInfoList = data.menuList.map { menu ->
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
            imgUrl = data.imageUrls.firstOrNull(),
        )
    } ?: emptyList()
}