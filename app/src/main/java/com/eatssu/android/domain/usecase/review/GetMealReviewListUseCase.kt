package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.common.enums.MenuType
import javax.inject.Inject

class GetMealReviewListUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        mealId: Long?,
    ): List<Review> =
        reviewRepository.getReviewList(MenuType.VARIABLE.toString(), mealId, 0)
}