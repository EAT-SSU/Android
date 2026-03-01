package com.eatssu.android.presentation.navigation

import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.ScreenId
import kotlinx.serialization.Serializable

object AppDestination {
    @Serializable
    object Intro

    @Serializable
    object ForceUpdate

    @Serializable
    object Login

    @Serializable
    object Main

    @Serializable
    object MyReview

    @Serializable
    object Developer

    @Serializable
    object Language

    @Serializable
    data class UserInfo(
        val force: Boolean = false,
    )

    @Serializable
    data class WebView(
        val url: String,
        val title: String,
        val screenId: ScreenId,
        val backIconResId: Int = -1,
    )

    @Serializable
    data class Review(
        val menuType: MenuType,
        val itemId: Long,
        val itemName: String,
    )

    @Serializable
    data class Report(
        val reviewId: Long,
    )

    @Serializable
    data class SignOut(
        val nickname: String = "",
    )
}

object MainDestination {
    @Serializable
    object Cafeteria

    @Serializable
    object Map

    @Serializable
    object MyPage
}

object ReviewDestination {
    @Serializable
    object List

    @Serializable
    object Write

    @Serializable
    data class Modify(
        val reviewId: Long,
        val initialRating: Int,
        val initialContent: String,
        val menuLikeInfoPayload: String,
    )
}

object MyReviewDestination {
    @Serializable
    object List

    @Serializable
    data class Modify(
        val reviewId: Long,
        val initialRating: Int,
        val initialContent: String,
        val menuLikeInfoPayload: String,
    )
}
