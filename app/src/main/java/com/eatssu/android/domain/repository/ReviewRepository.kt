package com.eatssu.android.domain.repository

import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import java.io.File

interface ReviewRepository {

    suspend fun writeMealReview(
        mealId: Long,
        rating: Int,
        content: String,
        imageUrls: List<String>,
        likeMenuIdList: List<Long>?,
    ): Boolean

    suspend fun writeMenuReview(
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

    suspend fun getMenuReviewList(
        menuId: Long?,
    ): List<Review>

    suspend fun getMealReviewList(
        mealId: Long?,
    ): List<Review>

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

}
