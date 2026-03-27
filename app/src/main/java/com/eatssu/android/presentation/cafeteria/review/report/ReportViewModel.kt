package com.eatssu.android.presentation.cafeteria.review.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.usecase.review.PostReportUseCase
import com.eatssu.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
@Inject constructor(
    private val postReportUseCase: PostReportUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ReportUiState> =
        MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()


    fun postData(reviewId: Long, reportType: String, content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            val success = postReportUseCase(reviewId, reportType, content)
            if (!success) {
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = true,
                        toastMessage = UiText.StringResource(R.string.toast_report_failed)
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    loading = false,
                    error = false,
                    isDone = true,
                    toastMessage = UiText.StringResource(R.string.toast_report_success)
                )
            }
        }
    }
}

data class ReportUiState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: UiText = UiText.Empty,
    var isDone: Boolean = false,
)
