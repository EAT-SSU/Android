package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.remote.dto.request.WriteMealReviewRequest
import com.eatssu.android.data.remote.dto.request.WriteMenuReviewRequest
import com.eatssu.android.domain.model.ReviewWriteData
import com.eatssu.android.domain.repository.ReviewRepository
import com.eatssu.common.enums.MenuType
import javax.inject.Inject

class WriteReviewUseCase @Inject constructor(
    private val reviewRepository: ReviewRepository,
) {
    suspend operator fun invoke(
        menuType: MenuType,
        itemId: Long,
        reviewData: ReviewWriteData
    ): Boolean {
        when (menuType) {
            MenuType.FIXED -> {
                val request = WriteMenuReviewRequest(
                    rating = reviewData.rating,
                    content = reviewData.content ?: "",
                    imageUrls = if (reviewData.imageUrl != null) arrayListOf(reviewData.imageUrl) else arrayListOf(),
                    menuLike = reviewData.likeMenuIdList?.let {
                        WriteMenuReviewRequest.MenuLike(
                            menuId = it.first(),
                            isLike = true,
                        )
                    }
                )
                return reviewRepository.writeMenuReview(request)
            }

            MenuType.VARIABLE -> {
                val request = WriteMealReviewRequest(
                    mealId = itemId,
                    rating = reviewData.rating,
                    content = reviewData.content ?: "",
                    imageUrls = if (reviewData.imageUrl != null) arrayListOf(reviewData.imageUrl) else arrayListOf(),
                    menuLikes = reviewData.likeMenuIdList?.map {
                        WriteMealReviewRequest.MenuLikes(
                            menuId = it,
                            isLike = true,
                        )
                    },
                )
                return reviewRepository.writeMealReview(request)
            }
        }
    }
}
