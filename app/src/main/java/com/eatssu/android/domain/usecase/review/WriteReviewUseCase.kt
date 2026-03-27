package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.common.enums.MenuType
import javax.inject.Inject

class WriteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        menuType: MenuType,
        itemId: Long,
        rating: Int,
        content: String,
        imageUrl: String?,
        likeMenuIdList: List<Long>?,
    ): Boolean {
        when (menuType) {
            MenuType.FIXED -> {
                return reviewRepository.writeMenuReview(
                    menuId = itemId,
                    rating = rating,
                    content = content,
                    imageUrls = if (imageUrl != null) listOf(imageUrl) else emptyList(),
                    likeMenuIdList = likeMenuIdList,
                )
            }

            MenuType.VARIABLE -> {
                return reviewRepository.writeMealReview(
                    mealId = itemId,
                    rating = rating,
                    content = content,
                    imageUrls = if (imageUrl != null) listOf(imageUrl) else emptyList(),
                    likeMenuIdList = likeMenuIdList,
                )
            }
        }
    }
}
