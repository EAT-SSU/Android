package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteMealReviewRequest
import com.eatssu.android.data.dto.request.WriteMenuReviewRequest
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
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
    )

    suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    )

    suspend fun getMenuReviewList(
        menuId: Long?,
    ): List<Review>

    suspend fun getMealReviewList(
        menuId: Long?,
    ): List<Review>

    suspend fun getMenuReviewInfo(
        menuId: Long,
    ): ReviewInfo

    suspend fun getMealReviewInfo(
        mealId: Long,
    ): ReviewInfo

    suspend fun getImageString(
        file: File
    ): String

    /**
     * MealId를 이용해서 Menu를 찾기 api (+ 리뷰 작성 가능한 메뉴 조회 v2)
     */
    suspend fun getMenuInfoByMealId(
        mealId: Long,
    ): List<Pair<Long, String>>
}
