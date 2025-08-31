package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.data.dto.response.toDomain
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(private val reviewService: ReviewService) :
    ReviewRepository {

    override suspend fun writeReview(
        menuId: Long,
        body: WriteReviewRequest,
    ): Flow<BaseResponse<Void>> =
        flow {
            emit(reviewService.writeReview(menuId, body))
        }

    override suspend fun deleteReview(reviewId: Long): Flow<BaseResponse<Void>> =
        flow {
            emit(reviewService.deleteReview(reviewId))
        }

    override suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Flow<BaseResponse<Void>> =
        flow {
            emit(reviewService.modifyReview(reviewId, body))
        }

    override suspend fun getMenuReviewList(menuId: Long?): Flow<List<Review>> = flow {

        reviewService.getMenuReviewList(menuId).result?.toDomain()?.let { emit(it) }
    }

    override suspend fun getMealReviewList(menuId: Long?): Flow<List<Review>> = flow {
        reviewService.getMealReviewList(menuId).result?.toDomain()?.let { emit(it) }
    }


    override suspend fun getMenuReviewInfo(menuId: Long): Flow<ReviewInfo> =
        flow {
            reviewService.getMenuReviewInfo(menuId).result?.toDomain()?.let { emit(it) }
        }

    override suspend fun getMealReviewInfo(mealId: Long): Flow<ReviewInfo> =
        flow {
            reviewService.getMealReviewInfo(mealId).result?.toDomain()?.let { emit(it) }
        }

    override suspend fun getImageString(file: File): Flow<BaseResponse<ImageResponse>> = flow {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val response = reviewService.uploadImage(multipart)
        emit(response)
    }

}
