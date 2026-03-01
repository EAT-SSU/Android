package com.eatssu.android.presentation.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.eatssu.android.domain.model.Menu
import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.android.domain.model.Section
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.ToastType

// ──────────────────────────────────────────────
// Sample Data
// ──────────────────────────────────────────────

object PreviewSampleData {

    val menu1 = Menu(
        id = 1L,
        name = "고구마치즈돈까스",
        price = 5000,
        rate = 4.5,
    )

    val menu2 = Menu(
        id = 2L,
        name = "김치찌개",
        price = 4000,
        rate = 3.8,
    )

    val menu3 = Menu(
        id = 3L,
        name = "제육볶음",
        price = 5500,
        rate = 4.2,
    )

    val menuList = listOf(menu1, menu2, menu3)

    val section = Section(
        menuType = MenuType.VARIABLE,
        cafeteria = Restaurant.HAKSIK,
        menuList = menuList,
        cafeteriaLocation = "학생회관 1층",
    )

    val sectionFixed = Section(
        menuType = MenuType.FIXED,
        cafeteria = Restaurant.FOOD_COURT,
        menuList = menuList,
        cafeteriaLocation = "학생회관 2층",
    )

    val sectionList = listOf(section, sectionFixed)

    val restaurantInfo = RestaurantInfo(
        enum = Restaurant.HAKSIK,
        name = "학생 식당",
        location = "학생회관 1층",
        image = "",
        time = "평일 11:00~14:00 / 17:00~18:30",
        etc = "주말 및 공휴일 휴무",
    )
}

// ──────────────────────────────────────────────
// PreviewParameterProviders
// ──────────────────────────────────────────────

class MenuPreviewParameterProvider : PreviewParameterProvider<Menu> {
    override val values: Sequence<Menu> = sequenceOf(
        PreviewSampleData.menu1,
        PreviewSampleData.menu2,
        PreviewSampleData.menu3,
    )
}

class SectionPreviewParameterProvider : PreviewParameterProvider<Section> {
    override val values: Sequence<Section> = sequenceOf(
        PreviewSampleData.section,
        PreviewSampleData.sectionFixed,
    )
}

class SectionListPreviewParameterProvider : PreviewParameterProvider<List<Section>> {
    override val values: Sequence<List<Section>> = sequenceOf(
        PreviewSampleData.sectionList,
        emptyList(),
    )
}

class RestaurantInfoPreviewParameterProvider : PreviewParameterProvider<RestaurantInfo?> {
    override val values: Sequence<RestaurantInfo?> = sequenceOf(
        PreviewSampleData.restaurantInfo,
        null,
    )
}

class BooleanPreviewParameterProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean> = sequenceOf(true, false)
}

class ToastTypePreviewParameterProvider : PreviewParameterProvider<ToastType> {
    override val values: Sequence<ToastType> = sequenceOf(
        ToastType.INFO,
        ToastType.SUCCESS,
        ToastType.WARNING,
        ToastType.ERROR,
    )
}

class RatingPreviewParameterProvider : PreviewParameterProvider<Int> {
    override val values: Sequence<Int> = sequenceOf(0, 1, 3, 5)
}
