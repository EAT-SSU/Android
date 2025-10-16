package com.eatssu.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class ModifyReviewRequest(
    @SerializedName("rating") val rating: Int? = null,
    @SerializedName("menuLikes") val menuLikes: List<MenuLikes> = arrayListOf(),
    @SerializedName("content") val content: String? = null
) {
    data class MenuLikes(

        @SerializedName("menuId") val menuId: Long? = null,
        @SerializedName("isLike") val isLike: Boolean? = null

    )
}