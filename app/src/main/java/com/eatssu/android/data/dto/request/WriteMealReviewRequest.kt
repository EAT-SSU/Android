package com.eatssu.android.data.dto.request

import com.google.gson.annotations.SerializedName


data class WriteMenuReviewRequest(
    @SerializedName("menuId") var menuId: Int? = null,
    @SerializedName("mainRating") var mainRating: Int? = null,
    @SerializedName("amountRating") var amountRating: Int? = null,
    @SerializedName("tasteRating") var tasteRating: Int? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("imageUrl") var imageUrl: String? = null,
    @SerializedName("menuLike") var menuLike: MenuLike? = MenuLike()
) {
    data class MenuLike(

        @SerializedName("menuId") var menuId: Long? = null,
        @SerializedName("isLike") var isLike: Boolean? = null

    )
}