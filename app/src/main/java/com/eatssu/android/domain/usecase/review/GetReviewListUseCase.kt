package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReviewListUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(menuType: MenuType, itemId: Long): Flow<List<Review>> =
        when (menuType) {
            MenuType.FIXED -> reviewRepository.getMenuReviewList(itemId)
            MenuType.VARIABLE -> reviewRepository.getMealReviewList(itemId)
        }
} 