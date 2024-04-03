package com.eatssu.android.data.service


import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface ImageService {

    @Multipart
    @POST("/reviews/upload/image") //리뷰 이미지 업로드
    suspend fun getImageUrl(
        @Part image: MultipartBody.Part,
    ): Call<BaseResponse<ImageResponse>>

}