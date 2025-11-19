package com.eatssu.android.domain.repository

import com.eatssu.android.data.remote.dto.request.ModifyReviewRequest
import com.eatssu.android.data.remote.dto.request.WriteReviewRequest
import com.eatssu.android.data.remote.dto.response.GetMealReviewInfoResponse
import com.eatssu.android.data.remote.dto.response.GetMenuReviewInfoResponse
import com.eatssu.android.data.remote.dto.response.ImageResponse
import com.eatssu.android.domain.model.Review
import java.io.File

interface ReviewRepository {

    suspend fun writeReview(
        menuId: Long,
        body: WriteReviewRequest,
    ): Boolean

    suspend fun deleteReview(
        reviewId: Long,
    ): Boolean

    suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Boolean

    suspend fun getReviewList(
        menuType: String,
        mealId: Long?,
        menuId: Long?,
    ): List<Review>

    suspend fun getMenuReviewInfo(
        menuId: Long,
    ): GetMenuReviewInfoResponse?


    suspend fun getMealReviewInfo(
        mealId: Long,
    ): GetMealReviewInfoResponse?

    suspend fun getImageString(
        file: File
    ): ImageResponse?
}