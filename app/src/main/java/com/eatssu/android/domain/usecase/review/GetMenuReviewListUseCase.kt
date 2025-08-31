package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMenuReviewListUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        menuId: Long?,
    ): Flow<List<Review>> =
        reviewRepository.getMenuReviewList(menuId)
}