package com.eatssu.android.domain.usecase.menu

import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class GetValidMenusOfMealUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(menuId: Long): List<Pair<Long, String>> {
        return reviewRepository.getValidMenusByMealId(menuId)
    }
}