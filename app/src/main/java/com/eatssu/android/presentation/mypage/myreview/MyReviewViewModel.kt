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

    fun getMyReviews() {
        viewModelScope.launch {
            val myReviewList = getMyReviewsUseCase()

            _uiState.value = UiState.Success(
                MyReviewState(
                    myReviews = myReviewList
                )
            )
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            deleteReviewUseCase(reviewId)
        }
    }
}


data class MyReviewState(
    var myReviews: List<Review>? = null,
    var isDeleted: Boolean = false,
    )