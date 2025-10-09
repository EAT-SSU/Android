package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.dto.response.GetMealReviewInfoResponse
import com.eatssu.android.data.dto.response.GetMenuReviewInfoResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.data.model.isSuccess
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.domain.repository.ReviewRepository
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
    ): Boolean =
        reviewService.writeReview(menuId, body).isSuccess()


    override suspend fun deleteReview(reviewId: Long): Boolean =
        reviewService.deleteReview(reviewId).isSuccess()

    override suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Boolean =
        reviewService.modifyReview(reviewId, body).isSuccess()

    override suspend fun getReviewList(
        menuType: String,
        mealId: Long?,
        menuId: Long?,
    ): GetReviewListResponse? =
        reviewService.getReviewList(menuType, mealId, menuId).orNull()

    override suspend fun getMenuReviewInfo(menuId: Long): GetMenuReviewInfoResponse? =
        reviewService.getMenuReviewInfo(menuId).orNull()

    override suspend fun getMealReviewInfo(mealId: Long): GetMealReviewInfoResponse? =
        reviewService.getMealReviewInfo(mealId).orNull()

    override suspend fun getImageString(file: File): ImageResponse? {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        return reviewService.uploadImage(multipart).orNull()
    }
}
