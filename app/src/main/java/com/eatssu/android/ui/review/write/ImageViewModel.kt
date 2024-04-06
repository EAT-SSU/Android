package com.eatssu.android.ui.review.write

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.usecase.GetImageUrlUseCase
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
//    (
//    private val imageService: ImageService,

) : ViewModel() {

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> get() = _imageUrl

    private var _imageFile = MutableLiveData<File>()
    val imageFile: LiveData<File> get() = _imageFile

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
            val requestFile = imageFile.value!!.asRequestBody("image/*".toMediaTypeOrNull())
            val multipart = MultipartBody.Part.createFormData(
                "image",
                imageFile.value!!.name,
                requestFile
            )
            viewModelScope.launch {
//                imageService.getImageUrl(multipart).enqueue(
//                    object : Callback<BaseResponse<ImageResponse>> {
//                        override fun onResponse(
//                            call: Call<BaseResponse<ImageResponse>>,
//                            response: Response<BaseResponse<ImageResponse>>,
//                        ) {
//                            if (response.isSuccessful) {
//                                // 정상적으로 통신이 성공된 경우
//                                _imageUrl.value = response.body()?.result?.url.toString()
//                                Log.d("ImageViewModel", "이미지 변환 완료")
//                                _uiState.update {
//                                    it.copy(
//                                        loading = false,
//                                        error = false,
//                                        isDone = true,
//                                        imgUrl = response.body()?.result?.url.toString()
//                                    )
//                                }
//                            } else {
//                                // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
//                                _uiState.update {
//                                    it.copy(
//                                        loading = false,
//                                        error = true,
//                                        isDone = false
//                                    )
//                                }
//                                Log.d(
//                                    "ImageViewModel",
//                                    "이미지 변환 실패" + response.body()?.code + response.body()?.message
//                                )
//                            }
//                        }
//
//                        override fun onFailure(
//                            call: Call<BaseResponse<ImageResponse>>,
//                            t: Throwable,
//                        ) {
//                            // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
//                            Log.d(
//                                "ImageViewModel",
//                                "onFailure 에러: " + t.message.toString()
//                            )
//                            _uiState.update {
//                                it.copy(
//                                    loading = false,
//                                    error = true,
//                                    isDone = false
//                                )
//                            }
//                        }
//                    })
//            }

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
                                isDone = true,
                                imgUrl = url.toString()
                            )
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
    var isDone: Boolean = false,
)