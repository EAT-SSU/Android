package com.eatssu.android.data.usecase.review

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMealReviewListUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        mealId: Long?,
    ): Flow<BaseResponse<GetReviewListResponse>> =
        reviewRepository.getReviewList(MenuType.VARIABLE.toString(), mealId, 0)
}