package com.eatssu.android.presentation.cafeteria.review.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.remote.dto.request.WriteReviewRequest
import com.eatssu.android.domain.usecase.review.GetImageUrlUseCase
import com.eatssu.android.domain.usecase.review.WriteReviewUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


@HiltViewModel
class UploadReviewViewModel @Inject constructor(
    private val writeReviewUseCase: WriteReviewUseCase,
    private val getImageUrlUseCase: GetImageUrlUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Init)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun postReview(menuId: Long, reviewData: WriteReviewRequest) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val success = writeReviewUseCase(menuId, reviewData)

            if (!success) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("리뷰 작성에 실패하였습니다."))
                return@launch
            }

            _uiState.value = UiState.Success(Unit)
            _uiEvent.emit(UiEvent.ShowToast("리뷰가 작성되었습니다."))
        }
    }

    suspend fun saveS3(file: File): String? {
        _uiState.value = UiState.Loading
        val url = getImageUrlUseCase(file)

        if (url == null) {
            _uiState.value = UiState.Error
            _uiEvent.emit(UiEvent.ShowToast("이미지 업로드에 실패하였습니다."))
            return null
        }

        _uiState.value = UiState.Success(Unit)
        return url
    }
}
