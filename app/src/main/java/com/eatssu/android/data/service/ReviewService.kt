package com.eatssu.android.data.service

import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.data.model.response.GetReviewInfoResponse
import com.eatssu.android.data.model.request.ModifyReviewRequest
import com.eatssu.android.data.model.response.GetChangedMenuInfoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ReviewService {
    @Multipart
    @POST("review/{menuId}/detail")
    fun uploadFiles(
        @Path("menuId") menuId: Int,
        @Part files: List<MultipartBody.Part>?,
        @Part("reviewCreate") reviewData: RequestBody?,
    ): Call<Void>


    @Multipart
    @POST("review/{menuId}/detail")
    fun uploadFiles(
        @Path("menuId") menuId: Int,
        @Part("reviewCreate") reviewData: RequestBody?,
    ): Call<Void>


    @DELETE("/review/{menuId}/detail/{reviewId}") //리뷰 삭제
    fun delReview(@Path("menuId") menuId: Int, @Path("reviewId") reviewId: Int): Call<String>

    @PATCH("/review/{menuId}/detail/{reviewId}") //리뷰 수정(글 수정)
    fun modifyReview(
        @Path("menuId") menuId: Int,
        @Path("reviewId") reviewId: Int,
        @Body request: ModifyReviewRequest
    ): Call<String>

    @GET("/review/{menuId}") //메뉴 리뷰 정보 조회(평점 등등)
    fun reviewInfo(@Path("menuId") menuId: Int): Call<GetReviewInfoResponse>

    @GET("review/{menuId}/list") //메뉴 리뷰 리스트 조회
    fun getReview(@Path("menuId") menuId: Int): Call<GetReviewListResponse>

}