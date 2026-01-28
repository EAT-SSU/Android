package com.eatssu.android.presentation.cafeteria.review.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetReviewListPagedUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewListViewModel @Inject constructor(
    private val getReviewInfoUseCase: GetReviewInfoUseCase,
    private val getReviewListPagedUseCase: GetReviewListPagedUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ReviewListState>>(UiState.Init)
    val uiState: StateFlow<UiState<ReviewListState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _loadParams = MutableStateFlow<Pair<MenuType, Long>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviewPagingData: Flow<PagingData<Review>> = _loadParams
        .filterNotNull()
        .flatMapLatest { (menuType, itemId) ->
            getReviewListPagedUseCase(menuType, itemId)
        }
        .cachedIn(viewModelScope)

    fun getReview(menuType: MenuType, itemId: Long) {
        // 파라미터 업데이트 시 페이징 흐름 트리거
        if (_loadParams.value?.first != menuType || _loadParams.value?.second != itemId) {
             _loadParams.value = menuType to itemId
        }
        
        viewModelScope.launch {
            loadReviewInfo(menuType, itemId)
        }
    }

    private suspend fun loadReviewInfo(menuType: MenuType, itemId: Long) {
        _uiState.value = UiState.Loading
        try {
            val reviewInfo = getReviewInfoUseCase(menuType, itemId)
            _uiState.value = UiState.Success(ReviewListState(reviewInfo))
        } catch (e: Exception) {
            _uiState.value = UiState.Error
            _uiEvent.emit(UiEvent.ShowToast("리뷰를 불러오지 못했습니다.", ToastType.ERROR))
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            val success = deleteReviewUseCase(reviewId)

            if (!success) {
                _uiEvent.emit(UiEvent.ShowToast("리뷰 삭제에 실패했습니다.", ToastType.ERROR))
                return@launch
            }

            _uiEvent.emit(UiEvent.ShowToast("리뷰를 삭제했습니다.", ToastType.SUCCESS))
            
            // 정보 갱신
            val currentParams = _loadParams.value
            if (currentParams != null) {
                loadReviewInfo(currentParams.first, currentParams.second)
                // 페이징 데이터 갱신을 위해 파라미터 재설정 (Paging 3 Adapter refresh 권장)
            }
        }
    }
}

data class ReviewListState(
    val reviewInfo: ReviewInfo? = null,
)
