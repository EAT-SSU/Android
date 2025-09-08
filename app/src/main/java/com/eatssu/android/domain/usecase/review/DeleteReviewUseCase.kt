package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(reviewId: Long) {
        reviewRepository.deleteReview(reviewId)
    }
}