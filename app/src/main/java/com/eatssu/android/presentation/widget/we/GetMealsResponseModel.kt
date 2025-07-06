package com.eatssu.android.presentation.widget.we

import com.eatssu.android.data.enums.Restaurant

// 각 식사별로 여러 메뉴 그룹을 지원하도록 구조 변경
// 예: lunch = ([ ["돈목살김치찜", "단호박카레볶음"], ["간짜장덮밥", "고추튀김"] ], "lunch")
data class GetMealsResponseModel(
    val breakfast: Pair<List<List<String>>, String>,
    val lunch: Pair<List<List<String>>, String>,
    val dinner: Pair<List<List<String>>, String>,
    val restaurant: Restaurant,
)
