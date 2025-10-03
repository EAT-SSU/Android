package com.eatssu.android.presentation.cafeteria.review.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.model.ReviewModifyData
import com.eatssu.android.domain.usecase.review.ModifyReviewUseCase
import com.eatssu.common.UiEvent
import com.eatssu.common.UiState
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

    private val _uiState = MutableStateFlow<UiState<ModifyState>>(UiState.Init)
    val uiState: StateFlow<UiState<ModifyState>> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<UiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    fun init(rating: Int, content: String, menus: List<Review.Menu>) {
        val base = ModifyState.Baseline(rating, content, menus)
        _uiState.value = UiState.Success(
            ModifyState.Editing(
                rating = rating, content = content, menus = menus, baseline = base
            )
        )
    }

    fun onRatingChanged(new: Int) = updateEditing { it.copy(rating = new) }
    fun onContentChanged(new: String) = updateEditing { it.copy(content = new) }
    fun toggleLike(id: Long) = updateEditing {
        it.copy(menus = it.menus.map { m -> if (m.menuId == id) m.copy(isLike = !m.isLike) else m })
    }

    private inline fun updateEditing(block: (ModifyState.Editing) -> ModifyState.Editing) {
        val cur = (_uiState.value as? UiState.Success)?.data as? ModifyState.Editing ?: return
        _uiState.value = UiState.Success(block(cur))
    }

    fun submit(reviewId: Long) {
        val editing = (_uiState.value as? UiState.Success)?.data as? ModifyState.Editing ?: return
        if (!editing.canSubmit) return

        _uiState.value = UiState.Success(
            ModifyState.Modifying(
                editing.rating,
                editing.content,
                editing.menus,
                editing.baseline
            )
        )

        viewModelScope.launch {
            try {
                modifyReviewUseCase(
                    reviewId,
                    ReviewModifyData(editing.rating, editing.content, editing.menus)
                )
                _uiEvent.emit(UiEvent.NavigateBack)
                _uiEvent.emit(UiEvent.ShowToast("리뷰를 수정했습니다."))

            } catch (e: Exception) {
                // 실패 시 다시 Editing 상태로 되돌림
                _uiState.value = UiState.Success(editing)
                _uiEvent.emit(UiEvent.ShowToast("리뷰 수정 실패: ${e.message}"))
            }
        }
    }
}

sealed class ModifyState {

    data class Baseline(
        val rating: Int,
        val content: String,
        val menus: List<Review.Menu>,
    )

    data class Editing(
        val rating: Int = 0,
        val content: String = "",
        val menus: List<Review.Menu> = emptyList(),
        val baseline: Baseline, // 초기 스냅샷
    ) : ModifyState() {
        val hasChanges: Boolean
            get() = rating != baseline.rating ||
                    content != baseline.content ||
                    menus != baseline.menus

        val canSubmit: Boolean
            get() = rating > 0 && hasChanges

        val contentCount: Int get() = content.length
    }

    data class Modifying(
        val rating: Int,
        val content: String,
        val menus: List<Review.Menu>,
        val baseline: Baseline, // 유지
    ) : ModifyState()
}
