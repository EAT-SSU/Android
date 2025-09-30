package com.eatssu.android.presentation.cafeteria.review.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewModifyData
import com.eatssu.android.domain.usecase.review.ModifyReviewUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyViewModel @Inject constructor(
    private val modifyReviewUseCase: ModifyReviewUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<ModifyState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<ModifyState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _menus = MutableStateFlow<List<Review.Menu>>(emptyList())
    val menus: StateFlow<List<Review.Menu>> = _menus

    fun setInitialMenus(initial: List<Review.Menu>) {
        _menus.value = initial
    }

    fun toggleLike(id: Long) {
        _menus.update { list ->
            list.map { m ->
                if (m.menuId == id) m.copy(isLike = !m.isLike) else m
            }
        }
    }

    fun modifyMyReview(
        reviewId: Long,
        rating: Int,
        content: String,
        menuLikes: List<Review.Menu>,
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val reviewData = ReviewModifyData(
                    rating = rating,
                    content = content,
                    menuLikes = menuLikes,
                )

                modifyReviewUseCase(reviewId, reviewData)
                _uiState.value = UiState.Success(ModifyState.ModifyDone)
            } catch (e: Exception) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("리뷰 수정에 실패했습니다: ${e.message}"))
            }
        }
    }
}

sealed class ModifyState {
    data object ModifyDone : ModifyState()
}