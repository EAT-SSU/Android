package com.eatssu.android.domain.usecase.review

import androidx.paging.PagingData
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.common.enums.MenuType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReviewListPagedUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    operator fun invoke(menuType: MenuType, itemId: Long?): Flow<PagingData<Review>> =
        when (menuType) {
            MenuType.FIXED -> reviewRepository.getMenuReviewListPaged(itemId)
            MenuType.VARIABLE -> reviewRepository.getMealReviewListPaged(itemId)
        }
}