package com.eatssu.android.data.service


import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.dto.response.GetMealReviewInfoResponse
import com.eatssu.android.data.dto.response.GetMenuReviewInfoResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.dto.response.ImageResponse
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
    @POST("/reviews/write/{menuId}") //리뷰 작성
    suspend fun writeReview(
        @Path("menuId") menuId: Long,
        @Body request: WriteReviewRequest,
    ): BaseResponse<Void>

    @DELETE("/reviews/{reviewId}") //리뷰 삭제
    suspend fun deleteReview(
        @Path("reviewId") reviewId: Long,
    ): BaseResponse<Void>

    @PATCH("/reviews/{reviewId}") //리뷰 수정(글 수정)
    suspend fun modifyReview(
        @Path("reviewId") reviewId: Long,
        @Body request: ModifyReviewRequest,
    ): BaseResponse<Void>

    //Todo paging 라이브러리 써보기
    @GET("/reviews") //리뷰 리스트 조회
    suspend fun getReviewList(
        @Query("menuType") menuType: String,
        @Query("mealId") mealId: Long?,
        @Query("menuId") menuId: Long?,
//        @Query("lastReviewId") lastReviewId: Long?,
        @Query("page") page: Int? = 0,
        @Query("size") size: Int? = 20,
        @Query("sort") sort: List<String>? = arrayListOf("date", "DESC"),
    ): BaseResponse<GetReviewListResponse>

    @GET("/reviews/menus/{menuId}") //고정 메뉴 리뷰 정보 조회(메뉴명, 평점 등등)
    suspend fun getMenuReviewInfo(
        @Path("menuId") menuId: Long,
    ): BaseResponse<GetMenuReviewInfoResponse>

    @GET("/reviews/meals/{mealId}") //식단(변동 메뉴) 리뷰 정보 조회(메뉴명, 평점 등등)
    suspend fun getMealReviewInfo(
        @Path("mealId") mealId: Long,
    ): BaseResponse<GetMealReviewInfoResponse>

    @Multipart
    @POST("/reviews/upload/image") //리뷰 이미지 업로드
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
    ): BaseResponse<ImageResponse>

}