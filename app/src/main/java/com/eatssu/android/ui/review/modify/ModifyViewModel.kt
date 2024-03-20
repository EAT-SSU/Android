package com.eatssu.android.ui.review.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.service.ReviewService
import com.eatssu.android.data.RetrofitImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<ModifyState> = MutableStateFlow(ModifyState())
    val uiState: StateFlow<ModifyState> = _uiState.asStateFlow()

    fun modifyMyReview(
        reviewId: Long,
        comment: String,
        mainGrade: Int,
        amountGrade: Int,
        tasteGrade: Int,
    ) {
        val service = RetrofitImpl.retrofit.create(ReviewService::class.java)

        val reviewData = ModifyReviewRequest(mainGrade, amountGrade, tasteGrade, comment)

        viewModelScope.launch(Dispatchers.IO) {
            service.modifyReview(reviewId, reviewData)
                .enqueue(object : Callback<BaseResponse<Void>> {
                    override fun onResponse(
                        call: Call<BaseResponse<Void>>,
                        response: Response<BaseResponse<Void>>,
                    ) {
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                _uiState.update {
                                    it.copy(
                                        loading = false,
                                        error = false,
                                        isDone = true,
                                        toastMessage = "수정이 완료되었습니다."
                                    )
                                }
                            } else {
                                _uiState.update {
                                    it.copy(
                                        loading = false,
                                        error = false,
                                        isDone = true,
                                        toastMessage = "수정이 실패하였습니다."
                                    )
                                }
                            }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Void>>, t: Throwable) {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = false,
                            isDone = true,
                            toastMessage = "수정이 실패하였습니다."
                        )
                    }
                }
                })
        }
    }
}

data class ModifyState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var toastMessage: String = "",

    var isDone: Boolean = false,

    )