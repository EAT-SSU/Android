package com.eatssu.android.presentation.navigation

import com.eatssu.android.domain.model.Review
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class MenuLikeInfoPayload(
    val menuId: Long,
    val name: String,
    val isLike: Boolean,
)

internal object ReviewModifyPayloadCodec {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun encode(menuLikeInfoList: List<Review.MenuLikeInfo>): String =
        json.encodeToString(
            menuLikeInfoList.map { menuLikeInfo ->
                MenuLikeInfoPayload(
                    menuId = menuLikeInfo.menuId,
                    name = menuLikeInfo.name,
                    isLike = menuLikeInfo.isLike,
                )
            }
        )

    fun decode(payload: String): ArrayList<Review.MenuLikeInfo> =
        runCatching {
            json.decodeFromString<List<MenuLikeInfoPayload>>(payload)
                .map { menuLikeInfo ->
                    Review.MenuLikeInfo(
                        menuId = menuLikeInfo.menuId,
                        name = menuLikeInfo.name,
                        isLike = menuLikeInfo.isLike,
                    )
                }
        }.getOrElse { emptyList() }
            .let(::ArrayList)
}
