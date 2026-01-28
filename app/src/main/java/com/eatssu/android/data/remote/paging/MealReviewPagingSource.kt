package com.eatssu.android.data.remote.paging

import com.eatssu.android.data.remote.service.ReviewService

class MealReviewPagingSource(
    reviewService: ReviewService,
    private val mealId: Long?
) : BaseReviewPagingSource(reviewService) {

    override suspend fun executeRequest(page: Int, size: Int) = 
        reviewService.getMealReviewList(
            mealId = mealId,
            page = page,
            size = size
        )
}