package com.eatssu.common.enums


enum class Restaurant(val value: String, val korean: String, val menuType: MenuType) {
    HAKSIK("haksik", "학생 식당", MenuType.VARIABLE),
    DODAM("dodam", "도담 식당", MenuType.VARIABLE),
    DORMITORY("dormitory", "기숙사 식당", MenuType.VARIABLE),
    FACULTY("faculty", "FACULTY (교직원 전용)", MenuType.VARIABLE),
    FOOD_COURT("food_court", "푸드 코트", MenuType.FIXED),
    SNACK_CORNER("snack_corner", "스낵 코너", MenuType.FIXED),
    THE_KITCHEN("the_kitchen", "더 키친", MenuType.FIXED);

    companion object {

        fun getVariableRestaurantList(): List<Restaurant> {
            return entries.filter { it.menuType == MenuType.VARIABLE }
        }
      
      fun fromRestaurantEnumName(enumName: String): String {
          return entries.find { it.name == enumName }?.korean ?: ""
        }

        fun fromKorean(name: String): Restaurant =
            entries.find { it.korean == name } ?: error("Unknown display name: $name")
    }
}