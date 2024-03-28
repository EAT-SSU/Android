package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteReviewRequest
import kotlinx.coroutines.flow.Flow
import retrofit2.http.PATCH

interface ReviewRepository {

    suspend fun writeReview(
        menuId: Long,
        body: WriteReviewRequest,
    ): Flow<BaseResponse<Void>>

    suspend fun deleteReview(
        reviewId: Long,
    ): Flow<BaseResponse<Void>>

    @PATCH("/review/{reviewId}") //리뷰 수정(글 수정)
    suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Flow<BaseResponse<Void>>

//    override suspend fun reissueToken(
//        refreshToken: String
//    ): Flow<ReissueResponse> = flow {
//        emit(authService.reissueToken(refreshToken))
//    }

//    suspend fun getMenuReviewInfo(menuId: Long)
//    : Flow<BaseResponse<GetMenuReviewInfoResponse>> =
//        flow {
//            emit(reviewService.getMenuReviewInfo(menuId))
//        }

}