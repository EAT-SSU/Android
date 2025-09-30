package com.eatssu.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class ModifyReviewRequest(
    @SerializedName("rating") var rating: Int? = null,
    @SerializedName("menuLikes") var menuLikes: List<MenuLikes> = arrayListOf(),
    @SerializedName("content") var content: String? = null
) {
    data class MenuLikes(

        @SerializedName("menuId") var menuId: Long? = null,
        @SerializedName("isLike") var isLike: Boolean? = null

    )
}