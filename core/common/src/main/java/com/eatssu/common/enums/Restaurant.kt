package com.eatssu.common.enums

import androidx.annotation.StringRes
import com.eatssu.common.R
import com.eatssu.common.UiText
import kotlinx.serialization.Serializable

@Serializable
enum class Restaurant(
    val value: String,
    @field:StringRes val displayNameResId: Int,
    @field:StringRes val locationResId: Int,
    @field:StringRes val timeResId: Int,
    @field:StringRes val etcResId: Int,
    val menuType: MenuType
) {
    HAKSIK(
        "haksik",
        R.string.restaurant_haksik,
        R.string.cafeteria_student_location,
        R.string.cafeteria_student_time,
        R.string.cafeteria_student_etc,
        MenuType.VARIABLE
    ),
    DODAM(
        "dodam",
        R.string.restaurant_dodam,
        R.string.cafeteria_dodam_location,
        R.string.cafeteria_dodam_time,
        R.string.cafeteria_dodam_etc,
        MenuType.VARIABLE
    ),
    DORMITORY(
        "dormitory",
        R.string.restaurant_dormitory,
        R.string.cafeteria_dormitory_location,
        R.string.cafeteria_dormitory_time,
        R.string.cafeteria_dormitory_etc,
        MenuType.VARIABLE
    ),
    FACULTY(
        "faculty",
        R.string.restaurant_faculty,
        R.string.cafeteria_faculty_location,
        R.string.cafeteria_faculty_time,
        R.string.cafeteria_faculty_etc,
        MenuType.VARIABLE
    ),
    SNACK_CORNER(
        "snack_corner",
        R.string.restaurant_snack_corner,
        R.string.cafeteria_snack_corner_location,
        R.string.cafeteria_snack_corner_time,
        R.string.cafeteria_snack_corner_etc,
        MenuType.FIXED
    ),
    THE_KITCHEN(
        "the_kitchen",
        R.string.restaurant_the_kitchen,
        R.string.cafeteria_the_kitchen_location,
        R.string.cafeteria_the_kitchen_time,
        R.string.cafeteria_the_kitchen_etc,
        MenuType.FIXED
    );

    /** ViewModel에서 Context 없이 사용하기 위한 UiText 변환 */
    fun toUiText(): UiText = UiText.StringResource(displayNameResId)
    fun toLocationUiText(): UiText = UiText.StringResource(locationResId)
    fun toTimeUiText(): UiText = UiText.StringResource(timeResId)
    fun toEtcUiText(): UiText = UiText.StringResource(etcResId)

    companion object {

        fun getVariableRestaurantList(): List<Restaurant> {
            return entries.filter { it.menuType == MenuType.VARIABLE }
        }
    }
}
