package com.eatssu.android.data.remote.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModifyReviewRequest(
    @SerialName("rating") val rating: Int? = null,
    @SerialName("menuLikes") val menuLikes: List<MenuLikes> = arrayListOf(),
    @SerialName("content") val content: String? = null
) {
    @Serializable
    data class MenuLikes(

        @SerialName("menuId") val menuId: Long? = null,
        @SerialName("isLike") val isLike: Boolean? = null

    )
}