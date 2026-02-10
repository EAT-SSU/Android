package com.eatssu.android.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//별점은 필수 값 나머지는 옵션
@Serializable
data class WriteMealReviewRequest(
    @SerialName("mealId") val mealId: Long,
    @SerialName("rating") val rating: Int,
    @SerialName("menuLikes") val menuLikes: List<MenuLikes>?,
    @SerialName("content") val content: String,
    @SerialName("imageUrls") val imageUrls: List<String>
) {
    @Serializable
    data class MenuLikes(
        @SerialName("menuId") val menuId: Long,
        @SerialName("isLike") val isLike: Boolean
    )
}
