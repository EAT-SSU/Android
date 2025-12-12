package com.eatssu.android.domain.usecase.menu

import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.repository.ReviewRepository
import javax.inject.Inject

class GetValidMenusOfMealUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository
) {
    suspend operator fun invoke(menuId: Long): List<MenuMini> {
        return reviewRepository.getValidMenusByMealId(menuId)
    }
}