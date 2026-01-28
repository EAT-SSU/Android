package com.eatssu.android.data.remote.paging

import com.eatssu.android.data.remote.service.ReviewService

class MenuReviewPagingSource(
    reviewService: ReviewService,
    private val menuId: Long?
) : BaseReviewPagingSource(reviewService) {

    override suspend fun executeRequest(page: Int, size: Int) =
        reviewService.getMenuReviewList(
            menuId = menuId,
            page = page,
            size = size
        )
}