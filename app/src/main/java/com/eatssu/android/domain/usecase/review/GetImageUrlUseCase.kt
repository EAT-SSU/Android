package com.eatssu.android.domain.usecase.review

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class GetImageUrlUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        image: MultipartBody.Part,
    ): Flow<BaseResponse<ImageResponse>> =
        reviewRepository.getImageString(image)
}