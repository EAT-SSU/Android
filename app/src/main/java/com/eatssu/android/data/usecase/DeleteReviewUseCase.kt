package com.eatssu.android.data.usecase

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(reviewId: Long): Flow<BaseResponse<Void>> =
        reviewRepository.deleteReview(reviewId)
}