package com.eatssu.android.data.dto.response

import com.eatssu.android.domain.model.Review
import com.google.gson.annotations.SerializedName

data class MealReviewListResponse(
    @SerializedName("numberOfElements") var numberOfElements: Int? = null,
    @SerializedName("hasNext") var hasNext: Boolean? = null,
    @SerializedName("dataList") var dataList: ArrayList<DataList> = arrayListOf()
) {
    data class DataList(
        @SerializedName("reviewId") var reviewId: Long? = null,
        @SerializedName("menuList") var menuList: ArrayList<MenuList> = arrayListOf(),
        @SerializedName("writerId") var writerId: Long? = null,
        @SerializedName("isWriter") var isWriter: Boolean? = null,
        @SerializedName("writerNickname") var writerNickname: String? = null,
        @SerializedName("rating") var rating: Int? = null,
        @SerializedName("writtenAt") var writtenAt: String? = null,
        @SerializedName("content") var content: String? = null,
        @SerializedName("imageUrls") var imageUrls: ArrayList<String> = arrayListOf(),
    ) {
        data class MenuList(
            @SerializedName("id") var id: Long? = null,
            @SerializedName("name") var name: String? = null,
            @SerializedName("isLike") var isLike: Boolean? = null,
        )
    }
}


fun MealReviewListResponse?.toDomain(): List<Review> {
    // MealReviewListResponse 객체 자체가 null이면 emptyList() 반환
    return this?.dataList?.map { data ->
        Review(
            reviewId = data.reviewId ?: -1L,
            isWriter = data.isWriter ?: false,
            menuList = data.menuList.map { menu ->
                Review.Menu(
                    menuId = menu.id ?: -1L,
                    name = menu.name ?: "",
                    isLike = menu.isLike ?: false
                )
            },
            writerNickname = data.writerNickname ?: "유저",
            rating = data.rating ?: 0,
            writeDate = data.writtenAt ?: "",
            content = data.content ?: "",
            imgUrl = data.imageUrls.firstOrNull(),
        )
    } ?: emptyList()
}