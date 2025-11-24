package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class WriteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(menuId: Long, body: WriteReviewRequest): Boolean =
        reviewRepository.writeReview(menuId, body)
}