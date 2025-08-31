package com.eatssu.android.domain.usecase.review

import com.eatssu.android.data.dto.request.WriteMealReviewRequest
import com.eatssu.android.data.dto.request.WriteMenuReviewRequest
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Result
import com.eatssu.android.domain.model.ReviewWriteData
import com.eatssu.android.domain.repository.ReviewRepository
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
                        mainRating = reviewData.rating,
                        content = reviewData.content,
                        imageUrl = reviewData.imageUrl,
                        menuLike = WriteMenuReviewRequest.MenuLike(
                            menuId = reviewData.menuLikes.firstOrNull(),
                            isLike = true,
                        )
                    )
                    reviewRepository.writeMenuReview(request)
                    Result.Success
                }

                MenuType.VARIABLE -> {
                    val request = WriteMealReviewRequest(
                        mealId = itemId.toInt(),
                        rating = reviewData.rating,
                        content = reviewData.content,
                        imageUrls = if (reviewData.imageUrl != null) arrayListOf(reviewData.imageUrl) else arrayListOf(),
                        menuLikes = reviewData.menuLikes.map { menuLike ->
                            WriteMealReviewRequest.MenuLikes(
                                menuId = menuLike,
                                isLike = true,
                            )
                        }
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