package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ReviewRepository {

    suspend fun writeReview(
        menuId: Long,
        body: WriteReviewRequest,
    ): Flow<BaseResponse<Void>> // todo : Flow<BaseResponse<Void>> 없애기

    suspend fun deleteReview(
        reviewId: Long,
    ): Flow<BaseResponse<Void>> // todo : Flow<BaseResponse<Void>> 없애기

    suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Flow<BaseResponse<Void>> // todo : Flow<BaseResponse<Void>> 없애기

    suspend fun getMenuReviewList(
        menuId: Long?,
    ): Flow<List<Review>>

    suspend fun getMealReviewList(
        menuId: Long?,
    ): Flow<List<Review>>

    suspend fun getMenuReviewInfo(
        menuId: Long,
    ): Flow<ReviewInfo>

    suspend fun getMealReviewInfo(
        mealId: Long,
    ): Flow<ReviewInfo>

    suspend fun getImageString(
        file: File
    ): Flow<BaseResponse<ImageResponse>>
}