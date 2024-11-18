package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.repository.ReviewRepository
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