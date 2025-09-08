package com.eatssu.android.domain.usecase.user

import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.repository.UserRepository
import javax.inject.Inject

class GetMyReviewsUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(): List<Review> =
        userRepository.getUserReviews()
}