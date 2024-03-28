package com.eatssu.android.ui.review.modify

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.data.usecase.ModifyReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
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
            modifyReviewUseCase(reviewId, body).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = false,
                        isDone = true,
                        toastMessage = "수정이 실패하였습니다."
                    )
                }
                Log.d(TAG, e.toString())
            }.collectLatest { result ->
                Log.d(TAG, result.toString())
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = false,
                        isDone = true,
                        toastMessage = "수정이 완료되었습니다."
                    )
                }
            }
        }
    }

    companion object {
        private val TAG = "ModifyViewModel"
    }
}

data class ModifyState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var toastMessage: String = "",

    var isDone: Boolean = false,

    )