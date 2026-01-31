package com.eatssu.android.domain.usecase.review

import com.eatssu.android.domain.repository.ReviewRepository
import java.io.File
import javax.inject.Inject

class GetImageUrlUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        file: File
    ): String? =
        reviewRepository.getImageString(file)
}