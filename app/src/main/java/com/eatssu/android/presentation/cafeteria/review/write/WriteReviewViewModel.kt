package com.eatssu.android.presentation.cafeteria.review.write

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.MenuMini
import com.eatssu.android.domain.usecase.menu.GetValidMenusOfMealUseCase
import com.eatssu.android.domain.usecase.review.GetImageUrlUseCase
import com.eatssu.android.domain.usecase.review.WriteReviewUseCase
import com.eatssu.common.EventLogger
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import id.zelory.compressor.Compressor
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
class WriteReviewViewModel @Inject constructor(
    private val writeReviewUseCase: WriteReviewUseCase,
    private val getImageUrlUseCase: GetImageUrlUseCase,
    private val getValidMenusOfMealUseCase: GetValidMenusOfMealUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<WriteReviewState>>(UiState.Init)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()


    fun loadMenuList(menuType: MenuType, id: Long, menuName: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val menuList: List<MenuMini> = when (menuType) {
                MenuType.FIXED -> listOf(
                    MenuMini(
                        id = id,
                        name = menuName
                    )
                )

                MenuType.VARIABLE -> getValidMenusOfMealUseCase(id)
            }
            _uiState.value = UiState.Success(
                WriteReviewState.Editing(
                    menuList = menuList,
                    rating = 0,
                    content = "",
                    likedMenuIds = emptySet(),
                    selectedImageUri = null
                )
            )
        }
    }

    fun onRatingChanged(new: Int) = updateEditing { it.copy(rating = new) }

    fun onContentChanged(new: String) = updateEditing { it.copy(content = new) }

    fun toggleLike(menuId: Long) = updateEditing { s ->
        val next =
            if (menuId in s.likedMenuIds) s.likedMenuIds - menuId else s.likedMenuIds + menuId
        s.copy(likedMenuIds = next)
    }

    fun setSelectedImage(uri: Uri?) = updateEditing { it.copy(selectedImageUri = uri) }

    private inline fun updateEditing(block: (WriteReviewState.Editing) -> WriteReviewState.Editing) {
        val cur = (_uiState.value as? UiState.Success)?.data as? WriteReviewState.Editing ?: return
        _uiState.value = UiState.Success(block(cur))
    }

    fun postReview(
        menuType: MenuType,
        itemId: Long,
        context: Context,
    ) {
        val editing =
            (_uiState.value as? UiState.Success)?.data as? WriteReviewState.Editing ?: return
        if (!editing.canSubmit) return

        // Posting 단계로 전이 (폼 보존)
        _uiState.value = UiState.Success(
            WriteReviewState.Posting(
                menuList = editing.menuList,
                rating = editing.rating,
                content = editing.content,
                likedMenuIds = editing.likedMenuIds,
                selectedImageUri = editing.selectedImageUri
            )
        )

        viewModelScope.launch {
            // 1) 이미지 업로드(있으면)
            var imageUrl: String? = null
            editing.selectedImageUri?.let { uri ->
                try {
                    val originalFile = uriToFile(uri, context)
                    if (originalFile.exists()) {
                        // 이미지 압축
                        val compressedFile = compressImage(context, originalFile)
                        if (compressedFile != null && compressedFile.exists()) {
                            imageUrl = getImageUrlUseCase(compressedFile)
                            _uiEvent.emit(UiEvent.ShowToast("이미지가 업로드되었습니다.", ToastType.SUCCESS))

                            // 원본 파일 삭제 (압축된 파일만 유지)
                            originalFile.delete()
                        } else {
                            _uiState.value = UiState.Success(editing) // 되돌림
                            _uiEvent.emit(UiEvent.ShowToast("이미지 압축에 실패하였습니다.", ToastType.ERROR))
                            return@launch
                        }
                    } else {
                        _uiState.value = UiState.Success(editing) // 되돌림
                        _uiEvent.emit(UiEvent.ShowToast("이미지 파일을 찾을 수 없습니다.", ToastType.ERROR))
                        return@launch
                    }
                } catch (e: Exception) {
                    Timber.e(e, "이미지 업로드 실패")
                    _uiState.value = UiState.Success(editing) // 되돌림
                    _uiEvent.emit(UiEvent.ShowToast("이미지 업로드에 실패하였습니다.", ToastType.ERROR))
                    return@launch
                }
            }

            // 2) 리뷰 작성
            val success = writeReviewUseCase(
                menuType = menuType,
                itemId = itemId,
                rating = editing.rating,
                content = editing.content,
                imageUrl = imageUrl,
                likeMenuIdList = editing.likedMenuIds.toList(),
            )

            if (!success) {
                _uiState.value = UiState.Success(editing) // 되돌림
                _uiEvent.emit(UiEvent.ShowToast("리뷰 작성에 실패하였습니다.", ToastType.ERROR))
                return@launch
            }

            // 리뷰 작성 완료 로깅
            EventLogger.completeReviewV2(
                rating = editing.rating.toLong(),
                likes = editing.likedMenuIds.size.toLong(),
                photoAttached = editing.selectedImageUri != null
            )

            _uiEvent.emit(UiEvent.ShowToast("리뷰가 작성되었습니다.", ToastType.SUCCESS))
            _uiEvent.emit(UiEvent.NavigateBack)
        }
    }
}

private fun uriToFile(uri: Uri, context: Context): File {
    val inputStream: InputStream = context.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open input stream for URI: $uri")

    val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
    val outputStream = FileOutputStream(file)

    inputStream.use { input -> outputStream.use { output -> input.copyTo(output) } }
    return file
}

private suspend fun compressImage(context: Context, originalFile: File): File? {
    return try {
        Compressor.compress(context, originalFile)
    } catch (e: Exception) {
        Timber.e(e, "이미지 압축 실패")
        null
    }
}


sealed class WriteReviewState {
    data class Editing(
        val menuList: List<MenuMini>,
        val rating: Int,
        val content: String,
        val likedMenuIds: Set<Long>,
        val selectedImageUri: Uri?,
    ) : WriteReviewState() {
        val canSubmit: Boolean get() = rating > 0
        val contentCount: Int get() = content.length
    }

    data class Posting(
        val menuList: List<MenuMini>,
        val rating: Int,
        val content: String,
        val likedMenuIds: Set<Long>,
        val selectedImageUri: Uri?,
    ) : WriteReviewState()

    // 성공시 상태는 정의하지 않음.
    // 성공시 네비게이트 이벤트 발생
}
