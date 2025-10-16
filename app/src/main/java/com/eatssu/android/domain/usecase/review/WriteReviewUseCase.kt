package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.request.WriteMealReviewRequest
import com.eatssu.android.data.dto.request.WriteMenuReviewRequest
import com.eatssu.android.domain.model.Result
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
    ): Result {
        return try {
            when (menuType) {
                MenuType.FIXED -> {
                    val request = WriteMenuReviewRequest(
                        rating = reviewData.rating,
                        content = reviewData.content,
                        imageUrls = if (reviewData.imageUrl != null) arrayListOf(reviewData.imageUrl) else arrayListOf(),
                        menuLike = reviewData.menuLikes?.let {
                            WriteMenuReviewRequest.MenuLike(
                                menuId = it.first(),
                                isLike = true,
                            )
                        }
                    )
                    reviewRepository.writeMenuReview(request)
                    Result.Success
                }

                MenuType.VARIABLE -> {
                    val request = WriteMealReviewRequest(
                        mealId = itemId,
                        rating = reviewData.rating,
                        content = reviewData.content,
                        imageUrls = if (reviewData.imageUrl != null) arrayListOf(reviewData.imageUrl) else arrayListOf(),
                        menuLikes = reviewData.menuLikes?.map {
                            WriteMealReviewRequest.MenuLikes(
                                menuId = it,
                                isLike = true,
                            )
                        },
                    )
                    reviewRepository.writeMealReview(request)
                    Result.Success
                }
            }
        } catch (e: Exception) {
            Result.Failure(e.message ?: "리뷰 작성에 실패했습니다.")
        }
    }
}