package com.eatssu.android.presentation.mypage.myreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetMyReviewsUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.common.UiEvent

import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
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
    private val getUserNickNameUseCase: GetUserNickNameUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<MyReviewUiState> = MutableStateFlow(MyReviewUiState.Loading)
    val uiState: StateFlow<MyReviewUiState> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    init {
        getMyReviewList()
    }

    fun loadUserNickname() {
        viewModelScope.launch {
            _nickname.value = getUserNickNameUseCase()
        }
    }

    fun getMyReviewList() {
        _uiState.value = MyReviewUiState.Loading

        viewModelScope.launch {
            val myReviewList = getMyReviewsUseCase()

            _uiState.value = if (myReviewList.isEmpty()) {
                MyReviewUiState.Empty
            } else {
                MyReviewUiState.Success(reviews = myReviewList)
            }
            // todo 에러처리
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
            _uiEvent.emit(
                UiEvent.ShowToast(
                    UiText.StringResource(R.string.toast_review_delete_success), ToastType.SUCCESS
                )
            )
            // 삭제 성공 시 내 리뷰 목록 재조회
            getMyReviewList()
        }
    }
}


sealed interface MyReviewUiState {
    data object Loading : MyReviewUiState
    data object Empty : MyReviewUiState
    data class Success(val reviews: List<Review>) : MyReviewUiState
    data object Error : MyReviewUiState
}