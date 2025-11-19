package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.request.WriteMealReviewRequest
import com.eatssu.android.data.dto.request.WriteMenuReviewRequest
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.data.remote.dto.request.ModifyReviewRequest
import java.io.File

interface ReviewRepository {

    suspend fun writeMenuReview(
        body: WriteMenuReviewRequest,
    )

    suspend fun writeMealReview(
        body: WriteMealReviewRequest,
    )

    suspend fun deleteReview(
        reviewId: Long,
    ): Boolean

    suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
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
    ): String

    suspend fun getValidMenusByMealId(
        mealId: Long,
    ): List<MenuMini>

    suspend fun getMyReviews(): List<Review>

}
