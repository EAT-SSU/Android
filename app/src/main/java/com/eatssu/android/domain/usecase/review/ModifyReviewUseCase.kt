package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class ModifyReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        reviewId: Long,
        rating: Int,
        content: String,
        menuLikeInfoList: List<Review.MenuLikeInfo>,
    ): Boolean {
        return reviewRepository.modifyReview(
            reviewId,
            rating,
            content,
            menuLikeInfoList
        )
    }
}