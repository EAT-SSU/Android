package com.eatssu.android.presentation.mypage.myreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetMyReviewsUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
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
    private val getUserNickNameUseCase: GetUserNickNameUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<MyReviewState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MyReviewState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _nickname = MutableStateFlow<String>("")
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
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            val myReviewList = getMyReviewsUseCase()
            _uiState.value = UiState.Success(
                if (myReviewList.isEmpty()) {
                    MyReviewState.NoReview
                } else {
                    MyReviewState.ReviewExists(myReviews = myReviewList)
                }
            )


//            try {
//                val myReviewList = getMyReviewsUseCase()
//
//            } catch (e: Exception) {
//                _uiState.value = UiState.Error
//                _uiEvent.emit(UiEvent.ShowToast("Error: $e"))
//                Timber.d("getMyReviewList: ${e.message}")
//            }
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            try {
                deleteReviewUseCase(reviewId)
                _uiEvent.emit(UiEvent.ShowToast("리뷰를 삭제했습니다."))
                // 삭제 성공 시 내 리뷰 목록 재조회
                getMyReviewList()
            } catch (e: Exception) {
                _uiEvent.emit(UiEvent.ShowToast("Error: $e"))
                Timber.d("deleteReview: ${e.message}")
            }

//            val success = deleteReviewUseCase(reviewId)
//            if (!success) {
//                _uiState.update {
//                    it.copy(
//                        loading = false,
//                        error = true,
//                        toastMessage = context.getString(R.string.delete_not)
//                    )
//                }
//                return@launch
//            }
//
//            _uiState.update {
//                it.copy(
//                    loading = false,
//                    error = false,
//                    isDeleted = true,
//                    toastMessage = context.getString(R.string.delete_done)
//                )
//            }
        }
    }
}


sealed class MyReviewState {
    data class ReviewExists(
        var myReviews: List<Review>? = null,
    ) : MyReviewState()

    data object NoReview : MyReviewState()
}