package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class GetImageUrlUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        file: File
    ): Flow<BaseResponse<ImageResponse>> =
        reviewRepository.getImageString(file)
}