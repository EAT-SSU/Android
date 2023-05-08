package com.eatssu.android.data.model.response

data class GetMyReviewResponse(
    val dataList: List<Data>,
    val hasNext: Boolean,
    val numberOfElements: Int
){
    data class Data(
        val content: String,
        val grade: Int,
        val imgUrlList: List<String>,
        val tagList: List<String>,
        val writeDate: String
    )
}