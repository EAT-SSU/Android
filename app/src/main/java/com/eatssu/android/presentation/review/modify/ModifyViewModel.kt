package com.eatssu.android.presentation.review.modify

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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
                        toastMessage = App.appContext.getString(R.string.modify_not)
                    )
                }
                Timber.e(e.toString())
            }.collectLatest { result ->
                Timber.d(result.toString())
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
}

data class ModifyState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var toastMessage: String = "",

    var isDone: Boolean = false,

    )