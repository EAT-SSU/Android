package com.eatssu.android.ui.review.write

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.data.usecase.WriteReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UploadReviewViewModel @Inject constructor(
    private val writeReviewUseCase: WriteReviewUseCase,
) : ViewModel() {

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
            writeReviewUseCase(
                menuId.value, reviewData.value
            ).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = true,
                        toastMessage = "리뷰 작성에 실패하였습니다.",
                        isUpload = false,
                    )
                }
                Log.d(TAG, e.toString())
            }.collectLatest { result ->
                Log.d(TAG, result.toString())
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = false,
                        toastMessage = "리뷰가 작성되었습니다.",
                        isUpload = true,
                    )
                }
            }
        }
    }

    companion object {
        private val TAG = "ReviewWriteViewModel"
    }
}

data class UploadReviewState(
    var toastMessage: String = "",
    var loading: Boolean = true,
    var error: Boolean = false,
    var isUpload: Boolean = false,
)