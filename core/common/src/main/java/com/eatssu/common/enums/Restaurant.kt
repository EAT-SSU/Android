package com.eatssu.common.enums


enum class Restaurant(val korean: String, val menuType: MenuType) {
    HAKSIK("학생 식당", MenuType.VARIABLE),
    DODAM("도담 식당", MenuType.VARIABLE),
    DORMITORY("기숙사 식당", MenuType.VARIABLE),
    FACULTY("FACULTY (교직원 전용)", MenuType.VARIABLE),
    FOOD_COURT("푸드 코트", MenuType.FIXED),
    SNACK_CORNER("스낵 코너", MenuType.FIXED),
    THE_KITCHEN("더 키친", MenuType.FIXED);

    companion object {

        fun getVariableRestaurantList(): List<Restaurant> {
            return entries.filter { it.menuType == MenuType.VARIABLE }
        }
      
      fun fromRestaurantEnumName(enumName: String): String {
          return entries.find { it.name == enumName }?.korean ?: ""
        }

        fun fromKorean(name: String): String =
            entries.find { it.korean == name }?.name ?: error("Unknown display name: $name")
    }
}