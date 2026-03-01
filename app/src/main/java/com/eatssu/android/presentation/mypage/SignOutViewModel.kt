package com.eatssu.android.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.SignOutUseCase
import com.eatssu.common.UiEvent

import com.eatssu.common.UiText
import com.eatssu.common.enums.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<SignOutUiState> = MutableStateFlow(SignOutUiState.Idle)
    val uiState: StateFlow<SignOutUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = SignOutUiState.Loading

            val success = signOutUseCase()
            if (!success) {
                _uiState.value = SignOutUiState.Error
                _uiEvent.emit(
                    UiEvent.ShowToast(
                        UiText.StringResource(R.string.toast_sign_out_fail),
                        ToastType.ERROR
                    )
                )
                return@launch
            }

            _uiState.value = SignOutUiState.SignedOut
            _uiEvent.emit(
                UiEvent.ShowToast(
                    UiText.StringResource(R.string.toast_sign_out_success),
                    ToastType.SUCCESS
                )
            )
            logoutUseCase() // 자동 로그인 정보 삭제
        }
    }
}

sealed interface SignOutUiState {
    data object Idle : SignOutUiState
    data object Loading : SignOutUiState
    data object SignedOut : SignOutUiState
    data object Error : SignOutUiState
}
