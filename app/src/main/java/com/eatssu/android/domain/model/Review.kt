package com.eatssu.android.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Review(
    val isWriter: Boolean,
    val reviewId: Long,
    val menuLikeInfoList: List<MenuLikeInfo>,
    val writerNickname: String,
    val rating: Int,
    val writeDate: String,
    val content: String,
    val imgUrl: String?,
) {
    @Parcelize
    data class MenuLikeInfo(
        val menuId: Long,
        val name: String,
        val isLike: Boolean,
    ) : Parcelable

}
