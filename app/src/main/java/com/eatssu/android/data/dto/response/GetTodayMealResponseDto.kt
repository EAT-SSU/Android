package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.Menu

typealias GetTodayMealResponseDto = List<GetTodayMeal>

data class GetTodayMeal(
    val mainRating: String,
    val mealId: Long,
    val menusInformation: List<MenusInformation>,
    val price: Long
){
    data class MenusInformation(
        val menuId: Long,
        val name: String
    )
}


//fun HomeResponse.asUserModel() = UserModel(
//    name = name,
//    pea = stat.pea,
//    spi = stat.spi,
//    str = stat.str,
//    kno = stat.skl,
//    level = level,
//    gender = when (characterType) {
//        "A" -> 1
//        "B" -> 2
//        else -> 3
//    },
//    exp = experiencePoint,
//    expLeft = exclusiveRange
//)
//
//
//fun GetTodayMeal.mapTodayMenuResponseToMenu(todayMealResponseDto: GetTodayMealResponseDto): List<Menu> {
//    return todayMealResponseDto.mapNotNull { todayMealResponseDto ->
//        val name = createNameList(todayMealResponseDto.menusInformation)
//        if (name.isNotEmpty()) {
//            Menu(
//                id = todayMealResponseDto.mealId,
//                name = name,
//                price = todayMealResponseDto.price, // Assuming price is Int in Menu
//                rate = todayMealResponseDto.mainRating
//            )
//        } else {
//            null
//        }
//    }
//}
//
//fun GetTodayMealResponseDto.asMenuList() = List<Menu>(
//    Menu(
//        id = GetTodayMeal.MenusInformation
//    )
//
//
//)
//
//
//    return todayMealResponseDto.mapNotNull { todayMealResponseDto ->
//        val name = createNameList(todayMealResponseDto.menusInformation)
//        if (name.isNotEmpty()) {
//            Menu(
//                id = todayMealResponseDto.mealId,
//                name = name,
//                price = todayMealResponseDto.price, // Assuming price is Int in Menu
//                rate = todayMealResponseDto.mainRating
//            )
//        } else {
//            null
//        }
//    }
//}