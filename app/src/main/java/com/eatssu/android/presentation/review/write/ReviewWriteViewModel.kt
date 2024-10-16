package com.eatssu.android.presentation.review.write

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.domain.service.ReviewService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UploadReviewViewModel(private val reviewService: ReviewService) : ViewModel() {

    private val _uiState: MutableStateFlow<UploadReviewState> =
        MutableStateFlow(UploadReviewState())
    val uiState: StateFlow<UploadReviewState> = _uiState.asStateFlow()

    private val _reviewData: MutableStateFlow<WriteReviewRequest> =
        MutableStateFlow(WriteReviewRequest())
    val reviewData: StateFlow<WriteReviewRequest> = _reviewData.asStateFlow()

    private val _menuId: MutableStateFlow<Long> = MutableStateFlow(-1)
    val menuId: StateFlow<Long> = _menuId.asStateFlow()

    fun setReviewData(
        menuId: Long,
        mainRating: Int,
        amountRating: Int,
        tasteRating: Int,
        comment: String,
        imageUrl: String,
    ) {
        _menuId.value = menuId
        _reviewData.value =
            WriteReviewRequest(mainRating, amountRating, tasteRating, comment, imageUrl)
    }


    fun postReview() {
        viewModelScope.launch {
            reviewService.writeReview(
                menuId.value, reviewData.value
            ).enqueue(object : Callback<BaseResponse<Void>> {
                override fun onResponse(
                    call: Call<BaseResponse<Void>>,
                    response: Response<BaseResponse<Void>>,
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.isSuccess == true) {

                            _uiState.update {
                                it.copy(
                                    loading = false,
                                    error = false,
                                    toastMessage = "리뷰가 작성되었습니다.",
                                    isUpload = true,
                                )
                            }

                            Log.d(
                                "UploadReviewViewModel",
                                "onResponse 리뷰 작성 성공: " + response.body().toString()
                            )

                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        _uiState.update {
                            it.copy(
                                loading = false,
                                error = true,
                                toastMessage = "리뷰 작성에 실패하였습니다.",
                                isUpload = false,
                            )
                        }
                        Log.d("UploadReviewViewModel", "onResponse 리뷰 작성 실패")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Void>>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = true,
                            toastMessage = "리뷰 작성에 실패하였습니다.",
                            isUpload = false,
                        )
                    }

                    Log.d("UploadReviewViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
        }
    }
}

data class UploadReviewState(
    var toastMessage: String = "",
    var loading: Boolean = true,
    var error: Boolean = false,
    var isUpload: Boolean = false,
)