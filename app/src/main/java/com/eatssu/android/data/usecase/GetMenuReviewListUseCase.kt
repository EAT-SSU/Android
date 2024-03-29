package com.eatssu.android.data.usecase

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.GetReviewListResponse
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.data.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMenuReviewListUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        menuId: Long?,
    ): Flow<BaseResponse<GetReviewListResponse>> =
        reviewRepository.getReviewList(MenuType.FIXED.toString(), 0, menuId)
}