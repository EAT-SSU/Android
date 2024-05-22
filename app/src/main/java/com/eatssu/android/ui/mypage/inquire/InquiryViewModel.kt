package com.eatssu.android.ui.mypage.inquire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.App
import com.eatssu.android.R
import com.eatssu.android.data.dto.request.InquiryRequest
import com.eatssu.android.data.usecase.PostInquiryUseCase
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
class InquireViewModel @Inject constructor(
    private val postInquiryUseCase: PostInquiryUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<InquireState> = MutableStateFlow(InquireState())
    val uiState: StateFlow<InquireState> = _uiState.asStateFlow()

    fun inquireContent(email: String, content: String) {
        Timber.d(email + content)

        viewModelScope.launch {
            postInquiryUseCase(InquiryRequest(email = email, content = content)).onStart {
                _uiState.update { it.copy(loading = true) }
            }.onCompletion {
                _uiState.update { it.copy(loading = false, error = true) }
            }.catch { e ->
                _uiState.update { it.copy(error = true, toastMessage = "정보를 불러올 수 없습니다.") }
                Timber.e(e.toString())
            }.collectLatest { result ->
                Timber.d(result.toString())

                result.let { it ->
                    if (it.isSuccess == true) {
                        _uiState.update {
                            it.copy(
                                done = true, toastMessage = App.appContext.getString(
                                    R.string.inquiry_send_done
                                )
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                done = false, toastMessage = App.appContext.getString(
                                    R.string.inquiry_send_not
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

data class InquireState(
    var loading: Boolean = true,
    var error: Boolean = false,
    var toastMessage: String = "",
    var done: Boolean = false,
)