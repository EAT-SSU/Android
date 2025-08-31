package com.eatssu.android.presentation.cafeteria.review.write

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
import kotlinx.coroutines.flow.catch
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
    private val getMenuNameListOfMealUseCase: GetMenuNameListOfMealUseCase,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<UiState<WriteReviewState>>(UiState.Success(WriteReviewState.Init))
    val uiState = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    // 메뉴 목록을 저장할 상태 추가
    private val _menuList = MutableStateFlow<List<Pair<Long, String>>>(emptyList())
    val menuList = _menuList.asStateFlow()

    fun findMenuItemByMealId(mealId: Long) {
        viewModelScope.launch {
            getMenuNameListOfMealUseCase(mealId)
                .catch { e ->
                    Timber.e("메뉴 목록 로드 실패: ${e.message}")
                    _menuList.value = emptyList()
                }
                .collect { response ->
                    response.result?.let { menuOfMealResponse ->
                        // `map` 함수에서 `Pair` 객체를 명시적으로 반환
                        val menuList = menuOfMealResponse.briefMenus.map { menuInfo ->
                            Pair(menuInfo.menuId, menuInfo.name)
                        }
                        _menuList.value = menuList
                        Timber.d("변동 메뉴 목록 로드 성공: $menuList")
                    } ?: run {
                        _menuList.value = emptyList()
                    }
                }
        }
    }

    fun postReview(
        menuType: MenuType,
        itemId: Long,
        rating: Int,
        content: String,
        menuLikes: List<Long>
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Success(WriteReviewState.Loading)

            val reviewData = ReviewWriteData(
                rating = rating,
                content = content,
                menuLikes = menuLikes
            )

            try {
                when (val result = writeReviewUseCase(menuType, itemId, reviewData)) {
                    is Result.Success -> {
                        _uiState.value = UiState.Success(WriteReviewState.Success)
                        _uiEvent.emit(UiEvent.ShowToast("리뷰가 작성되었습니다."))
                        // 성공 후 잠시 후 상태 초기화
                        kotlinx.coroutines.delay(1000)
                        _uiState.value = UiState.Success(WriteReviewState.Init)
                    }

                    is Result.Failure -> {
                        _uiState.value = UiState.Success(WriteReviewState.Error)
                        _uiEvent.emit(UiEvent.ShowToast(result.message))
                    }
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Success(WriteReviewState.Error)
                _uiEvent.emit(UiEvent.ShowToast("리뷰 작성에 실패하였습니다."))
                Timber.e(e)
            }
        }
    }

    suspend fun saveS3(file: File): String? {
        return getImageUrlUseCase(file)
            .onStart {
                _uiState.value = UiState.Success(WriteReviewState.Loading)
            }
            .catch { e ->
                _uiState.value = UiState.Success(WriteReviewState.Error)
                _uiEvent.emit(UiEvent.ShowToast("이미지 업로드에 실패하였습니다."))
                Timber.e(e)
            }
            .map { it.result?.url }
            .firstOrNull()
    }
}

sealed class WriteReviewState {
    object Init : WriteReviewState()
    object Loading : WriteReviewState()
    object Success : WriteReviewState()
    object Error : WriteReviewState()
}
