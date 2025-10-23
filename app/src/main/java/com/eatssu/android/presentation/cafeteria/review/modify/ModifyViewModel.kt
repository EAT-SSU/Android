package com.eatssu.android.presentation.cafeteria.review.modify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.App
import com.eatssu.android.R
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.domain.usecase.review.ModifyReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyViewModel @Inject constructor(
    private val modifyReviewUseCase: ModifyReviewUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ModifyState> = MutableStateFlow(ModifyState())
    val uiState: StateFlow<ModifyState> = _uiState.asStateFlow()

    fun modifyMyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val success = modifyReviewUseCase(reviewId, body)
            if (!success) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = true,
                        isDone = false,
                        toastMessage = App.appContext.getString(R.string.modify_not)
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    isDone = true,
                    toastMessage = App.appContext.getString(R.string.modify_done)
                )
            }
        }
    }
}

data class ModifyState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var toastMessage: String = "",

    var isDone: Boolean = false,

    )