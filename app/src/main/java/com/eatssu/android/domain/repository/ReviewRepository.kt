package com.eatssu.android.domain.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.dto.response.GetMealReviewInfoResponse
import com.eatssu.android.data.dto.response.GetMenuReviewInfoResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.dto.response.ImageResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface ReviewRepository {

    suspend fun writeReview(
        menuId: Long,
        body: WriteReviewRequest,
    ): Flow<BaseResponse<Void>>

    suspend fun deleteReview(
        reviewId: Long,
    ): Flow<BaseResponse<Void>>

    suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Flow<BaseResponse<Void>>

    suspend fun getReviewList(
        menuType: String,
        mealId: Long?,
        menuId: Long?,
    ): Flow<BaseResponse<GetReviewListResponse>>

    suspend fun getMenuReviewInfo(
        menuId: Long,
    ): Flow<BaseResponse<GetMenuReviewInfoResponse>>


    suspend fun getMealReviewInfo(
        mealId: Long,
    ): Flow<BaseResponse<GetMealReviewInfoResponse>>

    suspend fun getImageString(
        image: MultipartBody.Part,
    ): Flow<BaseResponse<ImageResponse>>
}