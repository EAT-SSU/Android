package com.eatssu.android.data.repository

import com.eatssu.android.domain.repository.ImageRepository
import com.eatssu.android.domain.service.ImageService
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(private val imageService: ImageService) :
    ImageRepository {


//    override suspend fun getImageString(
//       image: MultipartBody.Part
//    ): Flow<Unit> = flow {
//        emit(imageService.getImageUrl(image))
//    }


}