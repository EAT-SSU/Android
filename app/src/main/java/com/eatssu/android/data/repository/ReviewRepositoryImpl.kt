package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.dto.response.GetMealReviewInfoResponse
import com.eatssu.android.data.dto.response.GetMenuReviewInfoResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.android.data.service.ReviewService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(private val reviewService: ReviewService) :
    ReviewRepository {

    override suspend fun writeReview(
        menuId: Long,
        body: WriteReviewRequest,
    ): Flow<BaseResponse<Void>> =
        flow {
            emit(reviewService.writeReview(menuId, body))
        }

    override suspend fun deleteReview(reviewId: Long): Flow<BaseResponse<Void>> =
        flow {
            emit(reviewService.deleteReview(reviewId))
        }

    override suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Flow<BaseResponse<Void>> =
        flow {
            emit(reviewService.modifyReview(reviewId, body))
        }

    override suspend fun getReviewList(
        menuType: String,
        mealId: Long?,
        menuId: Long?,
    ): Flow<BaseResponse<GetReviewListResponse>> = flow {
        emit(reviewService.getReviewList(menuType, mealId, menuId))
    }

    override suspend fun getMenuReviewInfo(menuId: Long): Flow<BaseResponse<GetMenuReviewInfoResponse>> =
        flow {
            emit(reviewService.getMenuReviewInfo(menuId))
        }

    override suspend fun getMealReviewInfo(mealId: Long): Flow<BaseResponse<GetMealReviewInfoResponse>> =
        flow {
            emit(reviewService.getMealReviewInfo(mealId))
        }

    override suspend fun getImageString(
        image: MultipartBody.Part,
    ): Flow<BaseResponse<ImageResponse>> =
        flow {
            emit(reviewService.uploadImage(image))
        }

}
