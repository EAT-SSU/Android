package com.eatssu.android.data.usecase

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WriteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(menuId: Long, body: WriteReviewRequest): Flow<BaseResponse<Void>> =
        reviewRepository.writeReview(menuId, body)
}