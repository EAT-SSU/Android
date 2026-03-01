package com.eatssu.android.presentation.cafeteria.review.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.usecase.review.ModifyReviewUseCase
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
class ModifyViewModel @Inject constructor(
    private val modifyReviewUseCase: ModifyReviewUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ModifyUiState>(ModifyUiState.Loading)
    val uiState: StateFlow<ModifyUiState> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun init(rating: Int, content: String, menuLikeInfos: List<Review.MenuLikeInfo>) {
        val base = ModifyUiState.Baseline(rating, content, menuLikeInfos)
        _uiState.value = ModifyUiState.Editing(
            rating = rating, content = content, menuLikeInfos = menuLikeInfos, baseline = base
        )
    }

    fun onRatingChanged(new: Int) = updateEditing { it.copy(rating = new) }
    fun onContentChanged(new: String) = updateEditing { it.copy(content = new) }
    fun toggleLike(id: Long) = updateEditing {
        it.copy(menuLikeInfos = it.menuLikeInfos.map { m -> if (m.menuId == id) m.copy(isLike = !m.isLike) else m })
    }

    private inline fun updateEditing(block: (ModifyUiState.Editing) -> ModifyUiState.Editing) {
        val cur = _uiState.value as? ModifyUiState.Editing ?: return
        _uiState.value = block(cur)
    }

    fun submit(reviewId: Long) {
        val editing = _uiState.value as? ModifyUiState.Editing ?: return
        if (!editing.canSubmit) return

        _uiState.value = ModifyUiState.Submitting(
            editing.rating,
            editing.content,
            editing.menuLikeInfos,
            editing.baseline
        )

        viewModelScope.launch {
            val success = modifyReviewUseCase(
                reviewId, editing.rating, editing.content, editing.menuLikeInfos
            )
            if (!success) {
                _uiState.value = editing
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        UiText.StringResource(R.string.toast_review_modify_failed),
                        ToastType.ERROR
                    )
                )
                return@launch
            }

            _uiEvent.emit(UiEvent.NavigateBack)
            _uiEvent.emit(
                UiEvent.ShowToast(
                    UiText.StringResource(R.string.toast_review_modify_success),
                    ToastType.SUCCESS
                )
            )
        }
    }
}

sealed interface ModifyUiState {
    data object Loading : ModifyUiState

    data class Baseline(
        val rating: Int,
        val content: String,
        val menuLikeInfos: List<Review.MenuLikeInfo>,
    )

    data class Editing(
        val rating: Int = 0,
        val content: String = "",
        val menuLikeInfos: List<Review.MenuLikeInfo> = emptyList(),
        val baseline: Baseline,
    ) : ModifyUiState {
        val hasChanges: Boolean
            get() = rating != baseline.rating ||
                    content != baseline.content ||
                    menuLikeInfos != baseline.menuLikeInfos

        val canSubmit: Boolean
            get() = rating > 0 && hasChanges

        val contentCount: Int get() = content.length
    }

    data class Submitting(
        val rating: Int,
        val content: String,
        val menuLikeInfos: List<Review.MenuLikeInfo>,
        val baseline: Baseline,
    ) : ModifyUiState
}
