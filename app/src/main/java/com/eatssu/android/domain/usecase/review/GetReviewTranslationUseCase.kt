package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.model.ReviewTranslation
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class GetReviewTranslationUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        reviewId: Long,
        targetLanguage: String,
    ): ReviewTranslation? = reviewRepository.getReviewTranslation(reviewId, targetLanguage)
}
