package com.eatssu.android.data.dto.request


data class WriteReviewRequest(
    val mealId: Long,
    val rating: Long,
    val menuLikes: List<MenuLike>, //todo nullable?
    val content: String?,
    val imageUrls: List<String>?,
    )

data class MenuLike(
    val menuId: Long,
    val isLike: Boolean,
)