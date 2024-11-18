package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.GetMealReviewInfoResponse
import com.eatssu.android.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMealReviewInfoUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(mealId: Long): Flow<BaseResponse<GetMealReviewInfoResponse>> =
        reviewRepository.getMealReviewInfo(mealId)
}