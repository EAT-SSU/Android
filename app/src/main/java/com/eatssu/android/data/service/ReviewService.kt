package com.eatssu.android.data.service


import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.GetReviewInfoResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ReviewService {
    @Multipart
    @POST("reviews/{menuId}")
    fun writeReview(
        @Path("menuId") menuId: Long,
        @Part files: List<MultipartBody.Part>, // Remove the part name from the annotation
        @Part("reviewCreate") reviewData: RequestBody,
    ): Call<BaseResponse<Void>>

    @Multipart
    @POST("reviews/{menuId}")
    fun writeReview(
        @Path("menuId") menuId: Long,
        @Part("reviewCreate") reviewData: RequestBody,
    ): Call<BaseResponse<Void>>

    @DELETE("/reviews/{reviewId}") //리뷰 삭제
    fun delReview(@Path("reviewId") reviewId: Long): Call<BaseResponse<Void>>

    @PATCH("/review/{reviewId}") //리뷰 수정(글 수정)
    fun modifyReview(
        @Path("reviewId") reviewId: Long,
        @Body request: RequestBody,
    ): Call<BaseResponse<Void>>

    @GET("/reviews") //리뷰 리스트 조회
    fun getReviewList(
        @Query("menuType") menuType: String,
        @Query("mealId") mealId: Long?,
        @Query("menuId") menuId: Long?,
//        @Query("lastReviewId") lastReviewId: Long?,
//        @Query("page") page: Int?,
//        @Query("size") size: Int?,
//        @Query("black") black: List<String>?
    ): Call<BaseResponse<GetReviewListResponse>>

    @GET("/reviews/menus/{menuId}") //고정 메뉴 리뷰 정보 조회(메뉴명, 평점 등등)
    fun getMenuReviewInfo(
        @Query("menuType") menuType: String,
        @Path("menuId") menuId: Long,
    ): Call<BaseResponse<GetReviewInfoResponse>>

    @GET("/reviews/menus/{mealId}") //식단(변동 메뉴) 리뷰 정보 조회(메뉴명, 평점 등등)
    fun getMealReviewInfo(
        @Path("mealId") mealId: Long,
    ): Call<BaseResponse<GetReviewInfoResponse>>

}