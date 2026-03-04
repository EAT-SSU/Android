package com.eatssu.common.enums

import androidx.annotation.StringRes
import com.eatssu.common.R
import com.eatssu.common.UiText
import kotlinx.serialization.Serializable

@Serializable
enum class Restaurant(
    val value: String,
    @field:StringRes val displayNameResId: Int,
    val menuType: MenuType
) {
    HAKSIK("haksik", R.string.restaurant_haksik, MenuType.VARIABLE),
    DODAM("dodam", R.string.restaurant_dodam, MenuType.VARIABLE),
    DORMITORY("dormitory", R.string.restaurant_dormitory, MenuType.VARIABLE),
    FACULTY("faculty", R.string.restaurant_faculty, MenuType.VARIABLE),
    FOOD_COURT("food_court", R.string.restaurant_food_court, MenuType.FIXED),
    SNACK_CORNER("snack_corner", R.string.restaurant_snack_corner, MenuType.FIXED),
    THE_KITCHEN("the_kitchen", R.string.restaurant_the_kitchen, MenuType.FIXED);

    /** ViewModel에서 Context 없이 사용하기 위한 UiText 변환 */
    fun toUiText(): UiText = UiText.StringResource(displayNameResId)

    companion object {

        fun getVariableRestaurantList(): List<Restaurant> {
            return entries.filter { it.menuType == MenuType.VARIABLE }
        }
    }
}
