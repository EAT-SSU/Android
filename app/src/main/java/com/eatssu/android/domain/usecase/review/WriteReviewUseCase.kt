package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.model.ReviewWriteData
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.common.enums.MenuType
import javax.inject.Inject

class WriteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        menuType: MenuType,
        itemId: Long,
        reviewData: ReviewWriteData
    ): Boolean {
        when (menuType) {
            MenuType.VARIABLE -> {
                return reviewRepository.writeMealReview(
                    itemId,
                    reviewData.rating,
                    reviewData.content ?: "",
                    if (reviewData.imageUrl != null) listOf(reviewData.imageUrl) else emptyList(),
                    reviewData.likeMenuIdList,
                )
            }

            MenuType.FIXED -> {
                return reviewRepository.writeMenuReview(
                    reviewData.rating,
                    reviewData.content ?: "",
                    if (reviewData.imageUrl != null) listOf(reviewData.imageUrl) else emptyList(),
                    reviewData.likeMenuIdList,
                )
            }
        }
    }
}
