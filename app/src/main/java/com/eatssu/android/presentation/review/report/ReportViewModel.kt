package com.eatssu.android.presentation.review.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.data.dto.request.ReportRequest
import com.eatssu.android.domain.usecase.review.PostReportUseCase
import com.eatssu.android.presentation.mypage.usernamechange.UserNameChangeViewModel.Companion.TAG
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
class ReportViewModel
@Inject constructor(
    private val postReportUseCase: PostReportUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ReportUiState> =
        MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()


    fun postData(reviewId: Long, reportType: String, content: String) {
        viewModelScope.launch {
            postReportUseCase(ReportRequest(reviewId, reportType, content)).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "신고가 실패하였습니다.") }
                Log.e(TAG, e.toString())
            }.collectLatest { result ->
                _uiState.update { it.copy(isDone = true, toastMessage = "신고가 완료되었습니다.") }
            }
        }
    }
}

data class ReportUiState(
    var loading: Boolean = true,
    var error: Boolean = false,

    var toastMessage: String = "",
    var isDone: Boolean = false,
)
