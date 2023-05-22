package com.eatssu.android.data.service

import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.data.model.response.GetReviewInfoResponse
import com.eatssu.android.data.model.request.WriteReviewDetailRequest
import com.eatssu.android.data.model.request.ModifyReviewRequest
import com.eatssu.android.ui.review.Review
import retrofit2.Call
import retrofit2.http.*


interface ReviewService {

//    @FormUrlEncoded
//    @POST("review/{menuId}/detail")//리뷰 작성
//    fun writeReview(@Path("menuId") menuId: Int,
//                    @Body request: WriteReviewDetailRequest
//    ): Call<String>

    @Multipart
    @POST("review/{menuId}/detail")
    @Headers("Content-Type: multipart/form-data")
    fun writeReview(
        @Part("menuId") menuId: Int,
        @Part("request") request: WriteReviewDetailRequest
    ): Call<String>


    //by GPT
//    @Multipart
//    @POST("review/{menuId}/detail")
//    fun writeReview(
//        @Path("menuId") menuId: Int,
//        @Part image: Review,
//        @Part("reviewCreate") reviewCreate: WriteReviewDetailRequest
//    ): Call<String>

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