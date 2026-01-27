package com.eatssu.android.data.remote.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eatssu.android.data.model.isSuccess
import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orEmptyList
import com.eatssu.android.data.model.orNull
import com.eatssu.android.data.remote.dto.request.ModifyReviewRequest
import com.eatssu.android.data.remote.dto.request.WriteMealReviewRequest
import com.eatssu.android.data.remote.dto.request.WriteMenuReviewRequest
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.data.remote.paging.MealReviewPagingSource
import com.eatssu.android.data.remote.paging.MenuReviewPagingSource
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.repository.ReviewRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(private val reviewService: ReviewService) :
    ReviewRepository {

    override suspend fun writeMealReview(
        mealId: Long,
        rating: Int,
        content: String,
        imageUrls: List<String>,
        likeMenuIdList: List<Long>?,
    ): Boolean {
        val request = WriteMealReviewRequest(
            mealId = mealId,
            rating = rating,
            content = content,
            imageUrls = imageUrls,
            menuLikes = likeMenuIdList?.map {
                WriteMealReviewRequest.MenuLikes(
                    menuId = it,
                    isLike = true,
                )
            },
        )
        return reviewService.writeMealReview(request).isSuccess()
    }

    override suspend fun writeMenuReview(
        rating: Int,
        content: String,
        imageUrls: List<String>,
        likeMenuIdList: List<Long>?,
    ): Boolean {

        val request = WriteMenuReviewRequest(
            rating = rating,
            content = content,
            imageUrls = imageUrls,
            menuLike = likeMenuIdList?.let {
                WriteMenuReviewRequest.MenuLike(
                    menuId = it.first(),
                    isLike = true,
                )
            }
        )
        return reviewService.writeMenuReview(request).isSuccess()
    }

    override suspend fun deleteReview(reviewId: Long): Boolean =
        reviewService.deleteReview(reviewId).isSuccess()

    override suspend fun modifyReview(
        reviewId: Long,
        rating: Int,
        content: String,
        menuLikeInfoList: List<Review.MenuLikeInfo>,
    ): Boolean {

        val request = ModifyReviewRequest(
            rating = rating,
            content = content,
            menuLikes = menuLikeInfoList.map {
                ModifyReviewRequest.MenuLikes(
                    menuId = it.menuId,
                    isLike = it.isLike,
                )
            },
        )
        return reviewService.modifyReview(reviewId, request).isSuccess()
    }

    override suspend fun getMealReviewList(mealId: Long?): List<Review> {
        return reviewService.getMealReviewList(mealId).map { it.toDomain() }.orEmptyList()
    }

    override suspend fun getMenuReviewList(menuId: Long?): List<Review> {
        return reviewService.getMenuReviewList(menuId).map { it.toDomain() }.orEmptyList()
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

    override fun getMenuReviewListPaged(menuId: Long?): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { MenuReviewPagingSource(reviewService, menuId) }
        ).flow
    }

    override fun getMealReviewListPaged(mealId: Long?): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { MealReviewPagingSource(reviewService, mealId) }
        ).flow
    }
}
