package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.dto.request.WriteMealReviewRequest
import com.eatssu.android.data.dto.request.WriteMenuReviewRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.ImageResponse
import com.eatssu.android.data.dto.response.toDomain
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(private val reviewService: ReviewService) :
    ReviewRepository {

    override suspend fun writeMealReview(body: WriteMealReviewRequest) {
        val result = runCatching {
            reviewService.writeMealReview(body)
        }

        result.onSuccess { response ->
            if (response.isSuccess == true) {
                // API가 2xx 코드를 반환하는 등 성공적으로 처리된 경우
                // 여기에 성공 로직을 작성합니다.
                // 예: Unit 반환 또는 로그 기록
            } else {
                // API가 4xx, 5xx 등 실패 코드를 반환한 경우
                // 예외를 던져 onFailure 블록에서 처리하도록 유도합니다.
                throw Exception("리뷰 작성 실패: ${response.code}")
            }
        }.onFailure { e ->
            // 실패: 예외가 발생하면 이곳에서 처리
            // 예: throw Exception("리뷰 작성 실패: ${e.message}")
            throw e // 예외를 상위 계층으로 다시 던져서 처리
        }
    }


    override suspend fun writeMenuReview(body: WriteMenuReviewRequest) {
        val result = runCatching {
            reviewService.writeMenuReview(body)
        }

        result.onSuccess { response ->
            if (response.isSuccess == true) {
                // API가 2xx 코드를 반환하는 등 성공적으로 처리된 경우
                // 여기에 성공 로직을 작성합니다.
                // 예: Unit 반환 또는 로그 기록
            } else {
                // API가 4xx, 5xx 등 실패 코드를 반환한 경우
                // 예외를 던져 onFailure 블록에서 처리하도록 유도합니다.
                throw Exception("리뷰 작성 실패: ${response.code}")
            }
        }.onFailure { e ->
            // 실패: 예외가 발생하면 이곳에서 처리
            // 예: throw Exception("리뷰 작성 실패: ${e.message}")
            throw e // 예외를 상위 계층으로 다시 던져서 처리
        }
    }

    override suspend fun deleteReview(reviewId: Long): Flow<BaseResponse<Void>> =
        flow {
            emit(reviewService.deleteReview(reviewId))
        }

    override suspend fun modifyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ): Flow<BaseResponse<Void>> =
        flow {
            emit(reviewService.modifyReview(reviewId, body))
        }

    override suspend fun getMenuReviewList(menuId: Long?): Flow<List<Review>> = flow {

        reviewService.getMenuReviewList(menuId).result?.toDomain()?.let { emit(it) }
    }

    override suspend fun getMealReviewList(menuId: Long?): Flow<List<Review>> = flow {
        reviewService.getMealReviewList(menuId).result?.toDomain()?.let { emit(it) }
    }


    override suspend fun getMenuReviewInfo(menuId: Long): Flow<ReviewInfo> =
        flow {
            reviewService.getMenuReviewInfo(menuId).result?.toDomain()?.let { emit(it) }
        }

    override suspend fun getMealReviewInfo(mealId: Long): Flow<ReviewInfo> =
        flow {
            reviewService.getMealReviewInfo(mealId).result?.toDomain()?.let { emit(it) }
        }

    override suspend fun getImageString(file: File): Flow<BaseResponse<ImageResponse>> = flow {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val response = reviewService.uploadImage(multipart)
        emit(response)
    }

}
