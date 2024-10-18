package com.eatssu.android.data.repository

import com.eatssu.android.data.service.ImageService
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(private val imageService: ImageService) :
    ImageRepository {


//    override suspend fun getImageString(
//       image: MultipartBody.Part
//    ): Flow<Unit> = flow {
//        emit(imageService.getImageUrl(image))
//    }


}