package com.eatssu.android.ui.review.delete

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.App
import com.eatssu.android.R
import com.eatssu.android.data.usecase.DeleteReviewUseCase
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
class DeleteViewModel @Inject constructor(
    private val deleteReviewUseCase: DeleteReviewUseCase,
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
                        toastMessage = App.appContext.getString(R.string.delete_not)
                    )
                }
                Log.d(TAG, e.toString())
            }.collectLatest { result ->
                Log.d(TAG, result.toString())

                _uiState.update {
                    it.copy(
                        isDeleted = true,
                        toastMessage = App.appContext.getString(R.string.delete_done) //Todo Appcontext
                    )
                }
            }
        }
    }

    companion object {
        val TAG = "DeleteViewModel"
    }
}

data class DeleteState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var isDeleted: Boolean = false,

    )