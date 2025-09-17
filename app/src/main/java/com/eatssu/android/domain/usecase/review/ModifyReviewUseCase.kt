package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteMenuReviewRequest
import com.eatssu.android.domain.model.ReviewWriteData
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class ModifyReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        reviewId: Long,
        reviewData: ReviewWriteData,
    ) {
        val request = ModifyReviewRequest(
            content = reviewData.content,
            rating = reviewData.rating,
            menuLikes = WriteMenuReviewRequest.MenuLike(
                menuId = reviewData.menuLikes.firstOrNull(),
                isLike = true,
            )
        )
        reviewRepository.modifyReview(reviewId, request)
    }
}