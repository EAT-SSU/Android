package com.eatssu.android.data.enums

enum class Restaurant(val displayName: String, val menuType: MenuType) {
    HAKSIK("학생 식당", MenuType.VARIABLE),
    DODAM("도담 식당", MenuType.VARIABLE),
    DORMITORY("기숙사 식당", MenuType.VARIABLE),
    FOOD_COURT("푸드 코트", MenuType.FIXED),
    SNACK_CORNER("스낵 코너", MenuType.FIXED),
    THE_KITCHEN("더 키친", MenuType.FIXED)
}