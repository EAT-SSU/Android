package com.eatssu.android.presentation.cafeteria.review.modify

import androidx.lifecycle.ViewModel
import com.eatssu.android.data.dto.request.ModifyReviewRequest
import com.eatssu.android.domain.usecase.review.ModifyReviewUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ModifyViewModel @Inject constructor(
    private val modifyReviewUseCase: ModifyReviewUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ModifyState> = MutableStateFlow(ModifyState())
    val uiState: StateFlow<ModifyState> = _uiState.asStateFlow()

    suspend fun modifyMyReview(
        reviewId: Long,
        body: ModifyReviewRequest,
    ) {

        modifyReviewUseCase(reviewId, body)
        }

}

data class ModifyState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var toastMessage: String = "",

    var isDone: Boolean = false,

    )