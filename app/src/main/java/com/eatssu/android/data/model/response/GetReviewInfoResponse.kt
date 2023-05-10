package com.eatssu.android.data.model.response

data class GetReviewInfoResponse(
    val grade: Double,
    val menuName: String,
    val reviewGradeCnt: ReviewGradeCnt,
    val totalReviewCount: Int
){
    data class ReviewGradeCnt(
        val fiveCnt: Int,
        val fourCnt: Int,
        val oneCnt: Int,
        val threeCnt: Int,
        val twoCnt: Int
    )
}