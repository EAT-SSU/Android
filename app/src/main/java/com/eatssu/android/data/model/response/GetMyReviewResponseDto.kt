package com.eatssu.android.data.model.response

data class GetMyReviewResponseDto(
    val dataList: List<Data>,
    val hasNext: Boolean,
    val numberOfElements: Int
){
    data class Data(
        val content: String,
        val reviewId : Int,
        val mainGrade: Int,
        val amountGrade: Int,
        val tasteGrade : Int,
        val imgUrlList: List<String>,
        val writeDate: String,
        val menuName: String
    )
}