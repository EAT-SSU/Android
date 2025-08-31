package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMealReviewInfoUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(mealId: Long): Flow<ReviewInfo> =
        reviewRepository.getMealReviewInfo(mealId)
}