package com.eatssu.android.data.remote.dto.request

import com.google.gson.annotations.SerializedName

//별점은 필수 값 나머지는 옵션
data class WriteMealReviewRequest(
    @SerializedName("mealId") val mealId: Long,
    @SerializedName("rating") val rating: Int,
    @SerializedName("menuLikes") val menuLikes: List<MenuLikes>?,
    @SerializedName("content") val content: String,
    @SerializedName("imageUrls") val imageUrls: ArrayList<String>
) {
    data class MenuLikes(
        @SerializedName("menuId") val menuId: Long,
        @SerializedName("isLike") val isLike: Boolean
    )
}