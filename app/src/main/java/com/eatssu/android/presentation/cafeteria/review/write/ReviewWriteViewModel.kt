package com.eatssu.android.presentation.cafeteria.review.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.domain.usecase.review.GetImageUrlUseCase
import com.eatssu.android.domain.usecase.review.WriteReviewUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@HiltViewModel
class ReviewWriteViewModel @Inject constructor(
    private val writeReviewUseCase: WriteReviewUseCase,
    private val getImageUrlUseCase: GetImageUrlUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<UploadReviewState>>(UiState.Init)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun postReview(menuId: Long, reviewData: WriteReviewRequest) {
        viewModelScope.launch {
            writeReviewUseCase(menuId, reviewData)
                .onStart {
                    _uiState.value = UiState.Loading
                }
                .catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("리뷰 작성에 실패하였습니다."))
                    Timber.e(e)
                }
                .collectLatest {
                    _uiState.value = UiState.Success()
                    _uiEvent.emit(UiEvent.ShowToast("리뷰가 작성되었습니다."))
                }
        }
    }

    suspend fun saveS3(file: File): String? {
        return getImageUrlUseCase(file)
            .onStart {
                _uiState.value = UiState.Loading
            }
            .catch { e ->
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("이미지 업로드에 실패하였습니다."))
                Timber.e(e)
            }
            .map { it.result?.url }
            .firstOrNull()
    }
}

sealed class UploadReviewState
