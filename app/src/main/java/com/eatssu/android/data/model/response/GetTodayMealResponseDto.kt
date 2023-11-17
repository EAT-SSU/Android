package com.eatssu.android.data.model.response

typealias GetTodayMealResponseDto = List<GetTodayMeal>

data class GetTodayMeal(
    val mealId: Long,
    val price: Long,
    val mainGrade: Double,
    val changeMenuInfoList: List<ChangeMenuInfo>,
)