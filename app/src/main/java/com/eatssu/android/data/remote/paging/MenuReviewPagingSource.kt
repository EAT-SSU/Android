package com.eatssu.android.data.remote.paging

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.response.MenuReviewListResponse
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.ReviewService
import com.eatssu.android.domain.model.Review

class MenuReviewPagingSource(
    private val reviewService: ReviewService,
    private val menuId: Long?
) : BaseReviewPagingSource<MenuReviewListResponse>() {

    override suspend fun executeRequest(page: Int, size: Int): MenuReviewListResponse {
        val result = reviewService.getMenuReviewList(
            menuId = menuId,
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

    override fun MenuReviewListResponse.toReviewList(): List<Review> {
        return this.toDomain()
    }

    override fun MenuReviewListResponse.hasMorePages(): Boolean {
        return this.hasNext ?: false
    }
}
