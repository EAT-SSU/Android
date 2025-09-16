package com.eatssu.android.presentation.cafeteria.review.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetReviewListUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReviewListViewModel @Inject constructor(
    private val getReviewInfoUseCase: GetReviewInfoUseCase,
    private val getReviewListUseCase: GetReviewListUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ReviewListState>>(UiState.Init)
    val uiState: StateFlow<UiState<ReviewListState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    // 마지막 조회 파라미터 저장하여 삭제 후 재조회에 사용
    private var lastMenuType: MenuType? = null
    private var lastItemId: Long? = null

    fun getReview(menuType: MenuType, itemId: Long) {
        lastMenuType = menuType
        lastItemId = itemId
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val reviewInfo = getReviewInfoUseCase(menuType, itemId)
                val reviewList = getReviewListUseCase(menuType, itemId)

                _uiState.value = UiState.Success(
                    ReviewListState(
                        reviewInfo = reviewInfo,
                        reviewList = reviewList
                    )
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("Error: $e"))
            }
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            try {
                deleteReviewUseCase(reviewId)
                _uiEvent.emit(UiEvent.ShowToast("리뷰를 삭제했습니다."))
                // 삭제 성공 시 목록 재조회
                val type = lastMenuType
                val id = lastItemId
                if (type != null && id != null) {
                    getReview(type, id)
                }
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowToast("Error: $e"))
                Timber.d("deleteReview: ${e.message}")
            }
        }
    }
}

data class ReviewListState(
    val reviewInfo: ReviewInfo? = null,
    val reviewList: List<Review> = emptyList()
)