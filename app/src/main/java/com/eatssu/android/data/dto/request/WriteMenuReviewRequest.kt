package com.eatssu.android.data.dto.request

import com.google.gson.annotations.SerializedName

data class WriteMenuReviewRequest(
    @SerializedName("menuId") var menuId: Long? = null,
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

//todo api 수정되면 다시 살리기 (추석이후 api 수정 예정)
//data class WriteMenuReviewRequest(
//    @SerializedName("rating") var rating: Int? = null,
//    @SerializedName("menuLikes") var menuLikes: List<MenuLikes> = arrayListOf(),
//    @SerializedName("content") var content: String? = null,
//    @SerializedName("imageUrls") var imageUrls: List<String> = arrayListOf()
//
//) {
//    data class MenuLikes(
//        @SerializedName("menuId") var menuId: Long? = null,
//        @SerializedName("isLike") var isLike: Boolean? = null
//    )
//}