package com.eatssu.android.repository

import com.eatssu.android.data.enums.Restaurant
import com.eatssu.android.data.enums.Time
import com.eatssu.android.data.model.response.GetFixedMenuResponseDto
import com.eatssu.android.data.model.response.GetReviewInfoResponseDto
import com.eatssu.android.data.model.response.GetReviewListResponse
import com.eatssu.android.data.model.response.GetTodayMealResponseDto
import com.eatssu.android.data.service.MenuService
import com.eatssu.android.data.service.ReviewService
import retrofit2.Call
import retrofit2.Retrofit

// MenuRepository.kt

class ReviewListRepository(private val reviewService: ReviewService) {

    fun getReviewList(
        menuType: String,
        mealId: Long?,
        menuId: Long?,
        lastReviewId: Long?,
        page: Int?,
        size: Int?,
    ): Call<GetReviewListResponse> {
        return reviewService.getReviewList(menuType, mealId, menuId, lastReviewId, page, size)
    }

    fun getReviewInfo(
        menuType: String,
        mealId: Long?,
        menuId: Long?
    ): Call<GetReviewInfoResponseDto> {
        return reviewService.getRreviewInfo(menuType, mealId, menuId)
    }
}