package com.eatssu.android.presentation.widget.we

data class GetMealsResponseModel(
    val breakfast: Pair<List<String>, String>,
    val lunch: Pair<List<String>, String>,
    val dinner: Pair<List<String>, String>,
)
