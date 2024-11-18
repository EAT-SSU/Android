package com.eatssu.android.ui.review.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.usecase.review.GetImageUrlUseCase
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImageViewModel
@Inject constructor(
    private val getImageUrlUseCase: GetImageUrlUseCase,
) : ViewModel() {

    private val _imageUrl: MutableStateFlow<String> = MutableStateFlow("")
    val imageUrl: StateFlow<String> get() = _imageUrl

    private var _imageFile: MutableStateFlow<File?> = MutableStateFlow(null)
    val imageFile: StateFlow<File?> get() = _imageFile

    private val _uiState: MutableStateFlow<ImageState> = MutableStateFlow(ImageState())
    val uiState: StateFlow<ImageState> = _uiState.asStateFlow()


    fun setImageFile(imageFile: File) {
        _imageFile.value = imageFile
    }

    fun deleteFile() {
        _imageFile.value?.delete()
        _imageUrl.value = ""
    }


    fun saveS3() {
        if (imageFile.value?.exists() == true) {
            val requestFile = imageFile.value?.asRequestBody("image/*".toMediaTypeOrNull())
            val multipart = requestFile?.let {
                MultipartBody.Part.createFormData(
                    "image",
                    imageFile.value?.name,
                    it
                )
            }
            viewModelScope.launch {
                if (multipart != null) {
                    getImageUrlUseCase(multipart).onStart {
                        _uiState.update { it.copy(loading = true) }
                    }.onCompletion {
                        _uiState.update { it.copy(loading = false, error = true) }
                    }.catch { e ->
                        _uiState.update { it.copy(error = true, toastMessage = "이미지 변환에 실패하였습니다.") }
                        Timber.e(e.toString())
                    }.collectLatest { result ->
                        Timber.d(result.toString())
                        result.result?.apply {
                            _uiState.update {
                                it.copy(
                                    loading = false,
                                    error = false,
                                    isImageUploadDone = true,
                                    imgUrl = url.toString()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


data class ImageState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var toastMessage: String = "",

    var imgUrl: String = "",
    var isImageUploadDone: Boolean = false,
)