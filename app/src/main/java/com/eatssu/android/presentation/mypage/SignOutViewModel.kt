package com.eatssu.android.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.SignOutUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<SignOutState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<SignOutState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val success = signOutUseCase()
            if (!success) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("탈퇴에 실패했습니다."))
                return@launch
            }

            _uiState.value = UiState.Success(SignOutState(isSignOuted = true))
            _uiEvent.emit(UiEvent.ShowToast("탈퇴가 완료되었습니다."))
            logoutUseCase() // 자동 로그인 정보 삭제
        }
    }
}

data class SignOutState(
    val isSignOuted: Boolean = false,
)