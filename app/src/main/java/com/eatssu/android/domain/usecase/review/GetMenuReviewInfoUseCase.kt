package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.remote.dto.response.asReviewInfo
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class GetMenuReviewInfoUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(menuId: Long): ReviewInfo? =
        reviewRepository.getMenuReviewInfo(menuId)?.asReviewInfo()
}