package com.eatssu.android.data.usecase

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReviewListUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        menuType: String,
        mealId: Long?,
        menuId: Long?,
    ): Flow<BaseResponse<GetReviewListResponse>> =
        reviewRepository.getReviewList(menuType, mealId, menuId)
}