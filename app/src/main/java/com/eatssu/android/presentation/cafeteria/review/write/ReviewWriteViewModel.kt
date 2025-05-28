package com.eatssu.android.presentation.cafeteria.review.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.request.WriteReviewRequest
import com.eatssu.android.domain.usecase.review.GetImageUrlUseCase
import com.eatssu.android.domain.usecase.review.WriteReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
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

    private val _menuId = MutableStateFlow(-1L)
    val menuId = _menuId.asStateFlow()


    private var _imageFile: MutableStateFlow<File?> = MutableStateFlow(null)
    val imageFile: StateFlow<File?> get() = _imageFile


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

    suspend fun saveS3(compressedImage: File): String {
        Timber.d(compressedImage.toString())
        val requestFile = compressedImage.asRequestBody("image/*".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData(
            "image",
            imageFile.value?.name,
            requestFile
        )

        return getImageUrlUseCase(multipart)
            .onStart {
                _uiState.update { it.copy(loading = true) }
            }
            .onCompletion {
                _uiState.update { it.copy(loading = false) }
            }
            .catch { e ->
                _uiState.update {
                    it.copy(error = true, toastMessage = "이미지 업로드에 실패했습니다.")
                }
                Timber.e(e.toString())
                throw e // 예외 다시 던짐
            }
            .first() // 값을 기다림
            .result?.url ?: throw Exception("URL 변환 실패")
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