package com.eatssu.android.presentation.cafeteria.review.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.domain.usecase.review.GetImageUrlUseCase
import com.eatssu.android.domain.usecase.review.WriteReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject


@HiltViewModel
class UploadReviewViewModel @Inject constructor(
    private val writeReviewUseCase: WriteReviewUseCase,
    private val getImageUrlUseCase: GetImageUrlUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UploadReviewState())
    val uiState = _uiState.asStateFlow()

    fun postReview(menuId: Long, reviewData: WriteReviewRequest) {
        viewModelScope.launch {
            writeReviewUseCase(menuId, reviewData)
                .onStart {
                    _uiState.update { it.copy(loading = true) }
                }
                .onCompletion {
                    _uiState.update { it.copy(loading = false, error = true) }
                }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = true,
                            toastMessage = "리뷰 작성에 실패하였습니다.",
                            isUpload = false
                        )
                    }
                    Timber.e(e)
                }
                .collectLatest {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = false,
                            toastMessage = "리뷰가 작성되었습니다.",
                            isUpload = true
                        )
                    }
                }
        }
    }

    suspend fun saveS3(file: File): String? {
        return getImageUrlUseCase(file)
            .onStart {
                _uiState.update { it.copy(loading = true) }
            }
            .onCompletion {
                _uiState.update { it.copy(loading = false) }
            }
            .catch { e ->
                Timber.e(e)
                _uiState.update { it.copy(error = true, toastMessage = "업로드 실패") }
            }
            .map { it.result?.url }
            .firstOrNull()
    }
}

data class UploadReviewState(
    var toastMessage: String = "",
    var loading: Boolean = true,
    var error: Boolean = false,
    var imageUrl: String = "",
    var isUpload: Boolean = false,
    var isImageExist: Boolean = false,
    var isImageUploadDone: Boolean = false,
)