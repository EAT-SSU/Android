package com.eatssu.android.data.dto.response

data class GetReviewListResponse(
    val dataList: List<Data>?,
    val hasNext: Boolean,
    val numberOfElements: Int
){
    data class Data(
        val reviewId: Long,
        val menu: String,
        val writerId : Int,
        val isWriter: Boolean,
        val writerNickname: String,
        val mainGrade: Int,
        val amountGrade: Int,
        val tasteGrade: Int,
        val writeDate: String,
        val content: String,
        val imgUrlList: List<String>
    )
}