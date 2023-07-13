package com.eatssu.android.data.model.response

typealias GetTodayMealResponse = List<GetTodayMeal>;

data class GetTodayMeal(
    val mealId: Long,
    val price: Long,
    val mainGrade: Double,
    val changeMenuInfoList: List<ChangeMenuInfoList>,
)

data class ChangeMenuInfoList(
    val menuId: Long,
    val name: String,
)
