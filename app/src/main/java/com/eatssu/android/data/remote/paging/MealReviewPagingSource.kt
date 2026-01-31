package com.eatssu.android.data.remote.paging

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.MealReviewListResponse
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.domain.model.Review

class MealReviewPagingSource(
    private val reviewService: ReviewService,
    private val mealId: Long?
) : BaseReviewPagingSource<MealReviewListResponse>() {

    override suspend fun executeRequest(page: Int, size: Int): MealReviewListResponse {
        val result = reviewService.getMealReviewList(
            mealId = mealId,
            page = page,
            size = size
        )
        return when (result) {
            is ApiResult.Success -> result.data
            is ApiResult.Failure -> throw Exception(result.message)
            is ApiResult.NetworkError -> throw result.exception
            is ApiResult.UnknownError -> throw result.exception
        }
    }

    override fun MealReviewListResponse.toReviewList(): List<Review> {
        return this.toDomain()
    }

    override fun MealReviewListResponse.hasMorePages(): Boolean {
        return this.hasNext ?: false
    }
}
