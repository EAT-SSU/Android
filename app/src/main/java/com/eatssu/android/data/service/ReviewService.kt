package com.eatssu.android.data.service

import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.data.model.response.GetReviewInfoResponseDto
import com.eatssu.android.data.model.request.ModifyReviewRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ReviewService {
    @Multipart
    @POST("review/{menuId}")
    fun uploadFiles(
        @Path("menuId") menuId: Int,
        @Part files: List<MultipartBody.Part>, // Remove the part name from the annotation
        @Part("reviewCreate") reviewData: RequestBody,
    ): Call<Void>

    @Multipart
    @POST("review/{menuId}")
    fun uploadFiles(
        @Path("menuId") menuId: Int,
        @Part("reviewCreate") reviewData: RequestBody,
    ): Call<Void>


    @DELETE("/review/{menuId}/detail/{reviewId}") //리뷰 삭제
    fun delReview(@Path("menuId") menuId: Int, @Path("reviewId") reviewId: Int): Call<String>

    @PATCH("/review/{menuId}/detail/{reviewId}") //리뷰 수정(글 수정)
    fun modifyReview(
        @Path("menuId") menuId: Int,
        @Path("reviewId") reviewId: Int,
        @Body request: ModifyReviewRequest
    ): Call<String>

//    @GET("/review/info") //메뉴 리뷰 정보 조회(평점 등등)
//    fun reviewInfo(@Query("menuType") menuType: String, @Query("mealId") mealId: Int): Call<GetReviewInfoResponseDto>

    @GET("/review/info") //메뉴 리뷰 정보 조회(평점 등등)
    fun reviewInfo(@Query("menuType") menuType: String, @Query("menuId") menuId: Int): Call<GetReviewInfoResponseDto>

    @GET("review/{menuId}/list") //메뉴 리뷰 리스트 조회
    fun getReview(@Path("menuId") menuId: Int): Call<GetReviewListResponse>

}