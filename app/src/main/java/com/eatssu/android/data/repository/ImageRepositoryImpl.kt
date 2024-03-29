package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.data.service.ImageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(private val imageService: ImageService) :
    ImageRepository {

    override suspend fun getImageString(
        image: MultipartBody.Part,
    ): Flow<BaseResponse<ImageResponse>> =
        flow {
            emit(imageService.getImageUrl(image))
        }

}