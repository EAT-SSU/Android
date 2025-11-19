package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.isSuccess
import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.model.orNull
import com.eatssu.android.data.remote.dto.request.ModifyReviewRequest
import com.eatssu.android.data.remote.dto.request.WriteMealReviewRequest
import com.eatssu.android.data.remote.dto.request.WriteMenuReviewRequest
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.repository.ReviewRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(private val reviewService: ReviewService) :
    ReviewRepository {

    override suspend fun writeMealReview(body: WriteMealReviewRequest) {
        reviewService.writeMealReview(body).isSuccess()
    }

    override suspend fun writeMenuReview(body: WriteMenuReviewRequest) {
        reviewService.writeMenuReview(body).isSuccess()
    }

    override suspend fun deleteReview(reviewId: Long): Boolean =
        reviewService.deleteReview(reviewId).isSuccess()

    override suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Boolean =
        reviewService.modifyReview(reviewId, body).isSuccess()

    override suspend fun getMealReviewList(mealId: Long?): List<Review> {
        return reviewService.getMealReviewList(mealId).map { it.toDomain() }.orEmptyList()
    }

    override suspend fun getMenuReviewList(menuId: Long?): List<Review> {
        return reviewService.getMealReviewList(menuId).map { it.toDomain() }.orEmptyList()
    }

    override suspend fun getMealReviewInfo(mealId: Long): ReviewInfo? =
        reviewService.getMealReviewInfo(mealId).map { it.toDomain() }.orNull()

    override suspend fun getMenuReviewInfo(menuId: Long): ReviewInfo? =
        reviewService.getMenuReviewInfo(menuId).map { it.toDomain() }.orNull()

    override suspend fun getImageString(file: File): String? {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        return reviewService.uploadImage(multipart).map { it.url }.orNull()
    }

    override suspend fun getValidMenusByMealId(mealId: Long): List<MenuMini> {
        return reviewService.getMenuInfoByMealId(mealId).map { it.toDomain() }.orEmptyList()
    }

    override suspend fun getMyReviews(): List<Review> {
        return reviewService.getMyReviews().map { it.toDomain() }.orEmptyList()
    }
}
