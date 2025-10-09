package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.response.toReviewList
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.common.enums.MenuType
import javax.inject.Inject

class GetMenuReviewListUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        menuId: Long?,
    ): List<Review> =
        reviewRepository.getReviewList(MenuType.FIXED.toString(), 0, menuId)?.toReviewList()
            ?: emptyList()
}