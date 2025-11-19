package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.model.ReviewModifyData
import com.eatssu.android.data.remote.dto.request.ModifyReviewRequest
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class ModifyReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        reviewId: Long,
        reviewData: ReviewModifyData,
    ): Boolean {
        val request = ModifyReviewRequest(
            content = reviewData.content,
            rating = reviewData.rating,
            menuLikes = reviewData.menuLikeInfoList.map {
                ModifyReviewRequest.MenuLikes(
                    menuId = it.menuId,
                    isLike = it.isLike,
                )
            },
        )
        return reviewRepository.modifyReview(reviewId, request)
    }
}