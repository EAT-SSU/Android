package com.eatssu.android.data.usecase.auth

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.MyReviewResponse
import com.eatssu.android.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyReviewsUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): Flow<BaseResponse<MyReviewResponse>> =
        userRepository.getUserReviews()
}