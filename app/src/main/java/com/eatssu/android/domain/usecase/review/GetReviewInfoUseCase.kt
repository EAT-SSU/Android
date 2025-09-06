package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class GetReviewInfoUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(menuType: MenuType, itemId: Long): ReviewInfo =
        when (menuType) {
            MenuType.FIXED -> reviewRepository.getMenuReviewInfo(itemId)
            MenuType.VARIABLE -> reviewRepository.getMealReviewInfo(itemId)
        }
} 