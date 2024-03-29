package com.eatssu.android.data.usecase

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.data.repository.ImageRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class GetImageUrlUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
) {
    suspend operator fun invoke(
        image: MultipartBody.Part,
    ): Flow<BaseResponse<ImageResponse>> =
        imageRepository.getImageString(image)
}