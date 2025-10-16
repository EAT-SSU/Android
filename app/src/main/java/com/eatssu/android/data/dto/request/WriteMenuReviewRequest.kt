package com.eatssu.android.data.dto.request

import com.google.gson.annotations.SerializedName

//별점은 필수 값 나머지는 옵션
data class WriteMenuReviewRequest(
    @SerializedName("rating") val rating: Int,
    @SerializedName("menuLike") val menuLike: MenuLike? = MenuLike(),
    @SerializedName("content") val content: String? = null,
    @SerializedName("imageUrls") val imageUrls: List<String> = arrayListOf()

) {
    data class MenuLike(
        @SerializedName("menuId") val menuId: Long? = null,
        @SerializedName("isLike") val isLike: Boolean? = null
    )
}
