package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class ModifyReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Boolean =
        reviewRepository.modifyReview(reviewId, body)
}