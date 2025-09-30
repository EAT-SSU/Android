package com.eatssu.android.domain.model

data class Review(
    val isWriter: Boolean,
    val reviewId: Long,
    val menuList: List<Menu>,
    val writerNickname: String,
    val rating: Int,
    val writeDate: String,
    val content: String,
    val imgUrl: String?,
) {
    data class Menu(
        val menuId: Long,
        val name: String,
        val isLike: Boolean,
    )
}