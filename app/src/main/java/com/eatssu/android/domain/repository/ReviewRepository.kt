package com.eatssu.android.domain.repository

import androidx.paging.PagingData
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.model.ReviewTranslation
import java.io.File
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {

    suspend fun writeMealReview(
        mealId: Long,
        rating: Int,
        content: String,
        imageUrls: List<String>,
        likeMenuIdList: List<Long>?,
    ): Boolean

    suspend fun writeMenuReview(
        menuId: Long,
        rating: Int,
        content: String,
        imageUrls: List<String>,
        likeMenuIdList: List<Long>?,
    ): Boolean

    suspend fun deleteReview(
        reviewId: Long,
    ): Boolean

    suspend fun modifyReview(
        reviewId: Long,
        rating: Int,
        content: String,
        menuLikeInfoList: List<Review.MenuLikeInfo>,
    ): Boolean



    suspend fun getMenuReviewInfo(
        menuId: Long,
    ): ReviewInfo?

    suspend fun getMealReviewInfo(
        mealId: Long,
    ): ReviewInfo?

    suspend fun getImageString(
        file: File
    ): String?

    suspend fun getValidMenusByMealId(
        mealId: Long,
    ): List<MenuMini>

    suspend fun getMyReviews(): List<Review>

    suspend fun getReviewTranslation(
        reviewId: Long,
        targetLanguage: String,
    ): ReviewTranslation?

    fun getMenuReviewListPaged(menuId: Long?): Flow<PagingData<Review>>

    fun getMealReviewListPaged(mealId: Long?): Flow<PagingData<Review>>

}
