package com.eatssu.android.presentation.cafeteria.review.write

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Result
import com.eatssu.android.domain.model.ReviewWriteData
import com.eatssu.android.domain.usecase.menu.GetMenuNameListOfMealUseCase
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
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject


@HiltViewModel
class ReviewWriteViewModel @Inject constructor(
    private val writeReviewUseCase: WriteReviewUseCase,
    private val getImageUrlUseCase: GetImageUrlUseCase,
    private val getMenuNameListOfMealUseCase: GetMenuNameListOfMealUseCase,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<WriteReviewState>>(UiState.Init)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    // 이미지 관련 상태
    private val _selectedImageUri = MutableStateFlow<android.net.Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    private val _uploadedImageUrl = MutableStateFlow<String?>(null)
    val uploadedImageUrl = _uploadedImageUrl.asStateFlow()

    fun findMenuItemByMealId(mealId: Long) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            val menuList = getMenuNameListOfMealUseCase(mealId)
            _uiState.value = UiState.Success(
                WriteReviewState.ValidMenuListForReview(
                    menuList = menuList
                )
            )
        }
    }

    fun returnMenuItem(id: Long, menuName: String) {
        _uiState.value = UiState.Success(
            WriteReviewState.ValidMenuListForReview(
                menuList = listOf(Pair(id, menuName))
            )
        )
    }

    fun postReview(
        menuType: MenuType,
        itemId: Long,
        rating: Int,
        content: String,
        menuLikes: List<Long>,
        context: Context,
    ) {
        viewModelScope.launch {
            Timber.d("postReview 시작 - rating: $rating, content: $content, menuLikes: $menuLikes")
            _uiState.value = UiState.Loading

            var imageUrl: String? = null

            // 이미지가 선택된 경우 먼저 업로드
            val selectedUri = _selectedImageUri.value
            Timber.d("선택된 이미지 URI: $selectedUri")
            if (selectedUri != null) {
                try {
                    Timber.d("이미지 업로드 시작")
                    // Uri를 File로 변환 (ContentResolver 사용)
                    val file = uriToFile(selectedUri, context)
                    Timber.d("변환된 파일 경로: ${file.absolutePath}, 파일 존재: ${file.exists()}")
                    if (file.exists()) {
                        Timber.d("S3 업로드 시작")
                        imageUrl = saveS3(file)
                        Timber.d("S3 업로드 결과: $imageUrl")
                        _uploadedImageUrl.value = imageUrl
                        _uiEvent.emit(UiEvent.ShowToast("이미지가 업로드되었습니다."))
                        Timber.d("이미지 업로드 성공: $imageUrl")
                    } else {
                        _uiState.value = UiState.Error
                        _uiEvent.emit(UiEvent.ShowToast("이미지 파일을 찾을 수 없습니다."))
                        Timber.e("이미지 파일이 존재하지 않음: ${file.absolutePath}")
                        return@launch
                    }
                } catch (e: Exception) {
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("이미지 업로드에 실패하였습니다."))
                    Timber.e(e, "이미지 업로드 중 예외 발생")
                    return@launch
                }
            }

            val reviewData = ReviewWriteData(
                rating = rating,
                content = content,
                menuLikes = menuLikes,
                imageUrl = imageUrl
            )
            Timber.d("리뷰 데이터 생성: $reviewData")

            try {
                Timber.d("리뷰 작성 시작")
                when (val result = writeReviewUseCase(menuType, itemId, reviewData)) {
                    is Result.Success -> {
                        _uiState.value = UiState.Success(WriteReviewState.WriteDone)
                        _uiEvent.emit(UiEvent.ShowToast("리뷰가 작성되었습니다."))
                        // 성공 후 잠시 후 상태 초기화
//                        kotlinx.coroutines.delay(1000)
//                        _uiState.value = UiState.Success(WriteReviewState.Init)
                    }

                    is Result.Failure -> {
                        _uiState.value = UiState.Error
                        _uiEvent.emit(UiEvent.ShowToast(result.message))
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("리뷰 작성에 실패하였습니다."))
                Timber.e(e)
            }
        }
    }

    fun setSelectedImage(uri: android.net.Uri?) {
        _selectedImageUri.value = uri
    }


    private fun uriToFile(uri: android.net.Uri, context: Context): File {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream for URI: $uri")

        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        Timber.d("URI를 파일로 변환 완료: ${file.absolutePath}")
        return file
    }

    private suspend fun saveS3(file: File): String {
        Timber.d("saveS3 시작 - 파일: ${file.absolutePath}")
        return getImageUrlUseCase(file)
    }
}

sealed class WriteReviewState {
    data class ValidMenuListForReview(
        val menuList: List<Pair<Long, String>>
    ) : WriteReviewState()
    data object WriteDone : WriteReviewState()
}
