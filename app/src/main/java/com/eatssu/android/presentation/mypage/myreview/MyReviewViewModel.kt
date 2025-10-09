package com.eatssu.android.presentation.mypage.myreview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.model.Review
import com.eatssu.android.domain.usecase.review.DeleteReviewUseCase
import com.eatssu.android.domain.usecase.review.GetMyReviewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReviewViewModel @Inject constructor(
    private val getMyReviewsUseCase: GetMyReviewsUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState: MutableStateFlow<MyReviewState> = MutableStateFlow(MyReviewState())
    val uiState: StateFlow<MyReviewState> = _uiState.asStateFlow()

    init {
        getMyReviews()
    }

    fun getMyReviews() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val myReviewList = getMyReviewsUseCase()
            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    myReviews = myReviewList,
                    isEmpty = myReviewList.isEmpty()
                )
            }
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val success = deleteReviewUseCase(reviewId)
            if (!success) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = true,
                        toastMessage = context.getString(R.string.delete_not)
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    isDeleted = true,
                    toastMessage = context.getString(R.string.delete_done)
                )
            }
        }
    }
}


data class MyReviewState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",

    var isEmpty: Boolean = false,

    var myReviews: List<Review>? = null,
    var isDeleted: Boolean = false,

    )