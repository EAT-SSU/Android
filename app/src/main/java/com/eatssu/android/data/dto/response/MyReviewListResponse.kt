package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.Review
import com.google.gson.annotations.SerializedName

data class MyReviewListResponse(
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
        @SerializedName("menuList") var menuList: ArrayList<MenuList> = arrayListOf()
    ) {
        data class MenuList(
            @SerializedName("id") var id: Long? = null,
            @SerializedName("name") var name: String? = null,
            @SerializedName("isLike") var isLike: Boolean? = null
        )
    }
}

fun MyReviewListResponse.toDomain(): List<Review> {
    return dataList.map { data ->
        Review(
            reviewId = data.reviewId ?: 0,
            isWriter = true,
            menuList = data.menuList.map { menu ->
                Review.Menu(
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