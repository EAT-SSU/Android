package com.eatssu.android.data.service


import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.data.dto.response.MealReviewInfoResponse
import com.eatssu.android.data.dto.response.MealReviewListResponse
import com.eatssu.android.data.dto.response.MenuReviewInfoResponse
import com.eatssu.android.data.dto.response.MenuReviewListResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ReviewService {
    @POST("/v2/reviews/write/{menuId}") //리뷰 작성
    suspend fun writeReview(
        @Path("menuId") menuId: Long,
        @Body request: WriteReviewRequest,
    ): BaseResponse<Void>

    @DELETE("/v2/reviews/{reviewId}") //리뷰 삭제
    suspend fun deleteReview(
        @Path("reviewId") reviewId: Long,
    ): BaseResponse<Void>

    @PATCH("/v2/reviews/{reviewId}") //리뷰 수정(글 수정)
    suspend fun modifyReview(
        @Path("reviewId") reviewId: Long,
        @Body request: ModifyReviewRequest,
    ): BaseResponse<Void>

    @GET("/v2/reviews/list/meal") //리뷰 리스트 조회
    suspend fun getMealReviewList(
        @Query("mealId") mealId: Long?,
    ): BaseResponse<MealReviewListResponse>

    @GET("/v2/reviews/list/menu") //리뷰 리스트 조회
    suspend fun getMenuReviewList(
        @Query("mealId") mealId: Long?,
    ): BaseResponse<MenuReviewListResponse>

    @GET("/v2/reviews/statistics/meals/{menuId}") //고정 메뉴 리뷰 정보 조회(메뉴명, 평점 등등)
    suspend fun getMenuReviewInfo(
        @Path("menuId") menuId: Long,
    ): BaseResponse<MenuReviewInfoResponse>

    @GET("/v2/reviews/statistics/meals/{mealId}") //식단(변동 메뉴) 리뷰 정보 조회(메뉴명, 평점 등등)
    suspend fun getMealReviewInfo(
        @Path("mealId") mealId: Long,
    ): BaseResponse<MealReviewInfoResponse>

    @Multipart
    @POST("/reviews/upload/image") //리뷰 이미지 업로드
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
    ): BaseResponse<ImageResponse>

}