package com.eatssu.android.presentation.mypage.myreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetMyReviewsUseCase
import com.eatssu.android.domain.usecase.review.GetReviewTranslationUseCase
import com.eatssu.android.domain.usecase.user.GetUserNickNameUseCase
import com.eatssu.android.presentation.cafeteria.review.translation.ReviewTranslationUiState
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
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
    private val getReviewTranslationUseCase: GetReviewTranslationUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
) : ViewModel() {

    val isLoggedIn: Boolean
        get() = getAccessTokenUseCase().isNotBlank()

    private val _uiState: MutableStateFlow<UiState<MyReviewState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<MyReviewState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname

    private val _translationStates = MutableStateFlow<Map<Long, ReviewTranslationUiState>>(emptyMap())
    val translationStates: StateFlow<Map<Long, ReviewTranslationUiState>> =
        _translationStates.asStateFlow()

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
            _translationStates.value = _translationStates.value - reviewId
            getMyReviewList()
        }
    }

    fun toggleReviewTranslation(
        review: Review,
        targetLanguage: String,
    ) {
        val currentState = _translationStates.value[review.reviewId]
        if (currentState?.isLoading == true) return

        if (currentState?.translatedContent != null) {
            _translationStates.value = _translationStates.value + (
                review.reviewId to currentState.copy(isTranslated = !currentState.isTranslated)
                )
            return
        }

        viewModelScope.launch {
            _translationStates.value = _translationStates.value + (
                review.reviewId to ReviewTranslationUiState(isLoading = true)
                )

            val translation = getReviewTranslationUseCase(review.reviewId, targetLanguage)
            if (translation == null || translation.translatedContent.isBlank()) {
                _translationStates.value = _translationStates.value + (
                    review.reviewId to ReviewTranslationUiState(isUnavailable = true)
                )
                return@launch
            }

            if (translation.translatedContent.trim().equals(review.content.trim(), ignoreCase = true)) {
                _translationStates.value = _translationStates.value + (
                    review.reviewId to ReviewTranslationUiState(isUnavailable = true)
                    )
                return@launch
            }

            _translationStates.value = _translationStates.value + (
                review.reviewId to ReviewTranslationUiState(
                    translatedContent = translation.translatedContent,
                    isTranslated = true,
                )
                )
        }
    }
}


sealed class MyReviewState {
    data class ReviewExists(
        var myReviews: List<Review>? = null,
    ) : MyReviewState()

    data object NoReview : MyReviewState()
}
