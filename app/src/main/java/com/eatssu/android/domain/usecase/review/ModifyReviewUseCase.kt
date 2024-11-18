package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ModifyReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Flow<BaseResponse<Void>> =
        reviewRepository.modifyReview(reviewId, body)
}