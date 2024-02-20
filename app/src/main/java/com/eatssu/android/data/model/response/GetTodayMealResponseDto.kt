package com.eatssu.android.data.model.response

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