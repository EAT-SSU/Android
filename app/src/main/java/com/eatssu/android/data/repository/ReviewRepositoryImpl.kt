package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.service.ReviewService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

}
