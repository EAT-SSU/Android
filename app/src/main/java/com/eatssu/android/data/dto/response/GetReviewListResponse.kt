package com.eatssu.android.data.dto.response

import com.eatssu.android.data.model.Reviews

data class GetReviewListResponse(
    val dataList: List<Data>?,
    val hasNext: Boolean,
    val numberOfElements: Int,
) {
    data class Data(
        val reviewId: Long,
        val menu: String,
        val writerId: Int,
        val isWriter: Boolean,
        val writerNickname: String,
        val mainGrade: Int,
        val amountGrade: Int,
        val tasteGrade: Int,
        val writeDate: String,
        val content: String,
        val imgUrlList: List<String>,
    )
}

fun GetReviewListResponse.toReviewList(): List<Reviews> {
    return dataList.orEmpty().map { data ->
        Reviews(
            menu = data.menu,
            writerNickname = data.writerNickname,
            mainGrade = data.mainGrade,
            amountGrade = data.amountGrade,
            tasteGrade = data.tasteGrade,
            writeDate = data.writeDate,
            content = data.content,
            imgUrlList = data.imgUrlList
        )
    }
}