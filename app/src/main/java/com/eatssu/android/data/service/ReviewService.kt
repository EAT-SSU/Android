package com.eatssu.android.data.service

import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.data.model.response.GetReviewInfoResponseDto
import com.eatssu.android.data.model.request.ModifyReviewRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ReviewService {
    @Multipart
    @POST("review/{menuId}")
    fun uploadFiles(
        @Path("menuId") menuId: Long,
        @Part files: List<MultipartBody.Part>, // Remove the part name from the annotation
        @Part("reviewCreate") reviewData: RequestBody,
    ): Call<Void>

    @Multipart
    @POST("review/{menuId}")
    fun uploadFiles(
        @Path("menuId") menuId: Long,
        @Part("reviewCreate") reviewData: RequestBody,
    ): Call<Void>


    @DELETE("/review/{menuId}/detail/{reviewId}") //리뷰 삭제
    fun delReview(@Path("menuId") menuId: Int, @Path("reviewId") reviewId: Int): Call<String>

    @PATCH("/review/{menuId}/detail/{reviewId}") //리뷰 수정(글 수정)
    fun modifyReview(
        @Path("menuId") menuId: Int,
        @Path("reviewId") reviewId: Int,
        @Body request: ModifyReviewRequestDto
    ): Call<String>


    @GET("/review/info") // Retrieve menu review information (rating, etc.) for changeable menus
    fun getRreviewInfo(
        @Query("menuType") menuType: String,
        @Query("mealId") mealId: Long?,
        @Query("menuId") menuId: Long?,
    ): Call<GetReviewInfoResponseDto>


    @GET("/review/list") //메뉴 리뷰 리스트 조회 - 고정메뉴
    fun getReviewList(
        @Query("menuType") menuType: String,
        @Query("mealId") mealId: Long?,
        @Query("menuId") menuId: Long?,
//        @Query("lastReviewId") lastReviewId: Long?,
//        @Query("page") page: Int?,
//        @Query("size") size: Int?,
//        @Query("black") black: List<String>?
    ):Call<GetReviewListResponse>

}