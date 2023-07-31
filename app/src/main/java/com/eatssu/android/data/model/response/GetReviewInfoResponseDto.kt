package com.eatssu.android.data.model.response

data class GetReviewInfoResponseDto(
    val menuName: List<String>,
    val totalReviewCount: Int,
    val mainGrade: Int,
    val amountGrade: Int,
    val tasteGrade: Int,
    val reviewGradeCnt: ReviewGradeCnt,
){
    data class ReviewGradeCnt(
        val fiveCnt: Int,
        val fourCnt: Int,
        val oneCnt: Int,
        val threeCnt: Int,
        val twoCnt: Int
    )
}