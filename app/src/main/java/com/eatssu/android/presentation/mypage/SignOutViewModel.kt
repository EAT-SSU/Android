package com.eatssu.android.presentation.mypage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.domain.usecase.auth.LogoutUseCase
import com.eatssu.android.domain.usecase.auth.SignOutUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
import com.eatssu.android.presentation.util.ToastType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
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
            try {
                val isSignOut = signOutUseCase()
                if (isSignOut) {
                    _uiState.value = UiState.Success(SignOutState(isSignOuted = true))
                    _uiEvent.emit(
                        UiEvent.ShowToast(
                            context.getString(R.string.toast_sign_out_success),
                            ToastType.SUCCESS
                        )
                    )
                    logoutUseCase() // 자동 로그인 정보 삭제
                } else {
                    _uiState.value = UiState.Error
                    _uiEvent.emit(
                        UiEvent.ShowToast(
                            context.getString(R.string.toast_sign_out_success),
                            ToastType.ERROR
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("오류가 발생했습니다. $e", ToastType.ERROR))
            }
        }
    }
}

data class SignOutState(
    val isSignOuted: Boolean = false,
)