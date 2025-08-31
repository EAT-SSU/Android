package com.eatssu.android.presentation.cafeteria.review.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.enums.MenuType
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewInfo
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetMealReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetMealReviewListUseCase
import com.eatssu.android.domain.usecase.review.GetMenuReviewInfoUseCase
import com.eatssu.android.domain.usecase.review.GetMenuReviewListUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class ReviewListViewModel @Inject constructor(
    private val getMenuReviewInfoUseCase: GetMenuReviewInfoUseCase,
    private val getMenuReviewListUseCase: GetMenuReviewListUseCase,
    private val getMealReviewInfoUseCase: GetMealReviewInfoUseCase,
    private val getMealReviewListUseCase: GetMealReviewListUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ReviewListState>>(UiState.Init)
    val uiState: StateFlow<UiState<ReviewListState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun loadReview(
        menuType: MenuType,
        itemId: Long,
    ) {
        _uiState.value = UiState.Loading // Set loading state once at the start

        when (menuType) {
            MenuType.FIXED -> {
                callMenuReviewInfo(itemId)
                callMenuReviewList(itemId)
            }

            MenuType.VARIABLE -> {
                callMealReviewInfo(itemId)
                callMealReviewList(itemId)
            }
        }
    }

    private fun callMenuReviewInfo(menuId: Long) {
        viewModelScope.launch {
            getMenuReviewInfoUseCase(menuId)
                .catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("리뷰 정보를 불러오는데 실패하였습니다."))
                    Timber.d(e.toString())
                }
                .collect { result ->
                    // Use update to safely modify only the reviewInfo part of the state
                    _uiState.update { currentState ->
                        val data =
                            if (currentState is UiState.Success) currentState.data else ReviewListState()
                        UiState.Success(data?.copy(reviewInfo = result))
                    }
                }
        }
    }

    private fun callMealReviewInfo(itemId: Long) {
        viewModelScope.launch {
            getMealReviewInfoUseCase(itemId)
                .catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("리뷰 정보를 불러오는데 실패했습니다."))
                    Timber.e(e.toString())
                }
                .collect { result ->
                    _uiState.update { currentState ->
                        val data =
                            if (currentState is UiState.Success) currentState.data else ReviewListState()
                        UiState.Success(data?.copy(reviewInfo = result))
                    }
                }
        }
    }

    private fun callMenuReviewList(itemId: Long) {
        viewModelScope.launch {
            getMenuReviewListUseCase(itemId)
                .catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("리뷰 조회에 실패했습니다."))
                    Timber.e(e.toString())
                }
                .collect { result ->
                    _uiState.update { currentState ->
                        val data =
                            if (currentState is UiState.Success) currentState.data else ReviewListState()
                        // Ensure an empty list is used if no reviews are returned
                        val reviewList = if (result.isEmpty()) emptyList() else result
                        UiState.Success(data?.copy(reviewList = reviewList))
                    }
                }
        }
    }

    private fun callMealReviewList(itemId: Long) {
        viewModelScope.launch {
            getMealReviewListUseCase(itemId)
                .catch { e ->
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("리뷰 조회에 실패했습니다."))
                    Timber.e(e.toString())
                }
                .collect { result ->
                    _uiState.update { currentState ->
                        val data =
                            if (currentState is UiState.Success) currentState.data else ReviewListState()
                        // Ensure an empty list is used if no reviews are returned
                        val reviewList = if (result.isEmpty()) emptyList() else result
                        UiState.Success(data?.copy(reviewList = reviewList))
                    }
                }
        }
    }

//    fun deleteReview(reviewId: Long) {
//        viewModelScope.launch {
//            deleteReviewUseCase(reviewId).onStart {
//                _uiState.update { it.copy(loading = true) }
//            }.onCompletion {
//                _uiState.update { it.copy(loading = false, error = true) }
//            }.catch { e ->
//                _uiState.update {
//                    it.copy(
//                        error = true,
////                        toastMessage = context.getString(R.string.delete_not)
//                    )
//                }
//                Timber.e(e.toString())
//            }.collectLatest { result ->
//                Timber.d(result.toString())
//
//                _uiState.update {
//                    it.copy(
////                        isDeleted = true,
////                        toastMessage = context.getString(R.string.delete_done)
//                    )
//                }
//            }
//        }
//    }
}

data class ReviewListState(
    val reviewInfo: ReviewInfo? = null,
    val reviewList: List<Review>? = null
)