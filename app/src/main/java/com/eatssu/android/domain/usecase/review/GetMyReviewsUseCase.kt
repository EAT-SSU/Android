package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class GetMyReviewsUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(): List<Review> =
        reviewRepository.getUserReviews()
}
