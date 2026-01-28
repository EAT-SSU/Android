package com.eatssu.android.data.remote.paging

import com.eatssu.android.data.remote.service.ReviewService

import com.eatssu.android.data.remote.dto.response.MealReviewListResponse
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.domain.model.Review

class MealReviewPagingSource(
    reviewService: ReviewService,
    private val mealId: Long?
) : BaseReviewPagingSource<MealReviewListResponse>(reviewService) {

    override suspend fun executeRequest(page: Int, size: Int) =
        reviewService.getMealReviewList(
            mealId = mealId,
            page = page,
            size = size
        )

    override fun MealReviewListResponse.toReviewList(): List<Review> {
        return this.toDomain()
    }

    override fun MealReviewListResponse.hasMorePages(): Boolean {
        return this.hasNext ?: false
    }
}