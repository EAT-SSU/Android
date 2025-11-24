package com.eatssu.android.data.remote.dto.request

import com.google.gson.annotations.SerializedName

//별점은 필수 값 나머지는 옵션
data class WriteMenuReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("menuLike") val menuLike: MenuLike?,
    @SerializedName("content") val content: String,
    @SerializedName("imageUrls") val imageUrls: List<String>,
) {
    data class MenuLike(
        @SerializedName("menuId") val menuId: Long,
        @SerializedName("isLike") val isLike: Boolean
    )
}