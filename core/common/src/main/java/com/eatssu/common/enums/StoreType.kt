package com.eatssu.common.enums

import androidx.annotation.StringRes
import com.eatssu.common.R
import com.eatssu.common.UiText
import kotlinx.serialization.Serializable

@Serializable
enum class StoreType(
    val value: String,
    @field:StringRes val displayNameResId: Int,
) {
    CAFE("카페", R.string.category_school_partnership_cafe),
    RESTAURANT("음식점", R.string.category_school_partnership_restaurant),
    PUB("주점", R.string.category_school_partnership_pub);

    fun toUiText(): UiText = UiText.StringResource(displayNameResId)
}
