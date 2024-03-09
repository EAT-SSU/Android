package com.eatssu.android.data.dto.response

import com.google.gson.annotations.SerializedName

data class GetReviewListResponse(
    @SerializedName("numberOfElements") var numberOfElements: Int? = null,
    @SerializedName("hasNext") var hasNext: Boolean? = null,
    @SerializedName("dataList") var dataList: ArrayList<DataList> = arrayListOf(),
)

data class DataList(

    @SerializedName("reviewId") var reviewId: Int? = null,
    @SerializedName("menu") var menu: String? = null,
    @SerializedName("writerId") var writerId: Int? = null,
    @SerializedName("isWriter") var isWriter: Boolean? = null,
    @SerializedName("writerNickname") var writerNickname: String? = null,
    @SerializedName("mainRating") var mainRating: Int? = null,
    @SerializedName("amountRating") var amountRating: Int? = null,
    @SerializedName("tasteRating") var tasteRating: Int? = null,
    @SerializedName("writedAt") var writedAt: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("imageUrls") var imageUrls: ArrayList<String> = arrayListOf(),

    )

//fun GetReviewListResponse.toReviewList(): List<Reviews> {
//    return dataList.orEmpty().map { data ->
//        Reviews(
//            menu = data.menu,
//            writerNickname = data.writerNickname,
//            mainGrade = data.mainGrade,
//            amountGrade = data.amountGrade,
//            tasteGrade = data.tasteGrade,
//            writeDate = data.writeDate,
//            content = data.content,
//            imgUrlList = data.imgUrlList
//        )
//    }
//}