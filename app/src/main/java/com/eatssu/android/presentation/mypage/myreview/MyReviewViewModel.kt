package com.eatssu.android.presentation.mypage.myreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.user.GetMyReviewsUseCase
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
class MyReviewViewModel @Inject constructor(
    private val getMyReviewsUseCase: GetMyReviewsUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MyReviewState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MyReviewState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun getMyReviewList() {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val myReviewList = getMyReviewsUseCase()
                _uiState.value = UiState.Success(
                    if (myReviewList.isEmpty()) {
                        MyReviewState.NoReview
                    } else {
                        MyReviewState.ReviewExists(myReviews = myReviewList)
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("Error: $e"))
                Timber.d("getMyReviewList: ${e.message}")
            }
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            try {
                deleteReviewUseCase(reviewId)
                _uiEvent.emit(UiEvent.ShowToast("리뷰를 삭제했습니다."))
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowToast("Error: $e"))
                Timber.d("deleteReview: ${e.message}")
            }
        }
    }
}


sealed class MyReviewState {
    data class ReviewExists(
        var myReviews: List<Review>? = null,
    ) : MyReviewState()

    data object NoReview : MyReviewState()
}