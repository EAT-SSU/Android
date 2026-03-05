package com.eatssu.android.presentation.cafeteria.review.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetReviewListPagedUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
import com.eatssu.common.UiText
import com.eatssu.common.enums.MenuType
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _loadParams = MutableSharedFlow<Pair<MenuType, Long>>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviewPagingData: Flow<PagingData<Review>> = _loadParams
        .flatMapLatest { (menuType, itemId) ->
            getReviewListPagedUseCase(menuType, itemId)
        }
        .cachedIn(viewModelScope)

    fun getReview(menuType: MenuType, itemId: Long) {
        // 동일 파라미터로 다시 진입(작성/수정 후 popBackStack)해도
        // 항상 페이징 소스를 새로 만들 수 있도록 SharedFlow로 트리거한다.
        _loadParams.tryEmit(menuType to itemId)

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
            _uiEvent.emit(
                UiEvent.ShowToast(
                    UiText.StringResource(R.string.toast_review_load_failed),
                    ToastType.ERROR
                )
            )
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            val success = deleteReviewUseCase(reviewId)

            if (!success) {
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        UiText.StringResource(R.string.toast_review_delete_failed),
                        ToastType.ERROR
                    )
                )
                return@launch
            }

            _uiEvent.emit(ReviewListEvent.ReviewDeleted)

            // 정보 갱신
            val currentParams = _loadParams.replayCache.lastOrNull()
            if (currentParams != null) loadReviewInfo(currentParams.first, currentParams.second)
        }
    }
}

data class ReviewListState(
    val reviewInfo: ReviewInfo? = null,
)


sealed interface ReviewListEvent : UiEvent {
    object ReviewDeleted : ReviewListEvent
}
