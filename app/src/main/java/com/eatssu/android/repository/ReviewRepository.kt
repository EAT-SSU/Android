package com.eatssu.android.repository

import com.eatssu.android.data.model.response.GetReviewInfoResponseDto
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.data.service.ReviewService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

// MenuRepository.kt

class ReviewRepository(private val reviewService: ReviewService) {

    fun getReviewList(
        menuType: String,
        mealId: Long?,
        menuId: Long?,
//        lastReviewId: Long?,
//        page: Int?,
//        size: Int?,
    ): Call<GetReviewListResponse> {
        return reviewService.getReviewList(menuType, mealId, menuId)
    }

    fun getReviewInfo(
        menuType: String,
        mealId: Long?,
        menuId: Long?
    ): Call<GetReviewInfoResponseDto> {
        return reviewService.getRreviewInfo(menuType, mealId, menuId)
    }

    fun writeReview(
        menuId: Long,
        files: List<MultipartBody.Part>,
        reviewData: RequestBody
    ): Call<Void> {
        return reviewService.writeReview(menuId, files, reviewData)
    }

    fun writeReview(
        menuId: Long,
        reviewData: RequestBody
    ): Call<Void> {
        return reviewService.writeReview(menuId, reviewData)
    }
}