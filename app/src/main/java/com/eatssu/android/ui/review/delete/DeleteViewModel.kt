package com.eatssu.android.ui.review.delete

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
class DeleteViewModel @Inject constructor(
    private val deleteReviewUseCase: DeleteReviewUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState: MutableStateFlow<DeleteState> = MutableStateFlow(DeleteState())
    val uiState: StateFlow<DeleteState> = _uiState.asStateFlow()


    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            deleteReviewUseCase(reviewId).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        error = true,
                        toastMessage = context.getString(R.string.delete_not)
                    )
                }
                Timber.e(e.toString())
            }.collectLatest { result ->
                Timber.d(result.toString())

                _uiState.update {
                    it.copy(
                        isDeleted = true,
                        toastMessage = context.getString(R.string.delete_done)
                    )
                }
            }
        }
    }
}

data class DeleteState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var isDeleted: Boolean = false,

    )