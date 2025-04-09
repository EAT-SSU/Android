package com.eatssu.android.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetIsAccessTokenValidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getIsAccessTokenValidUseCase: GetIsAccessTokenValidUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<IntroUiState> = MutableStateFlow(IntroUiState.Loading)
    val uiState: StateFlow<IntroUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<IntroUiEvent>()
    val uiEvent: SharedFlow<IntroUiEvent> = _uiEvent

    init {
        autoLogin()
    }

    private fun autoLogin() {
        viewModelScope.launch {
            _uiState.value = IntroUiState.Loading

            try {
                // 토큰 존재 여부 확인
                if (getAccessTokenUseCase().isEmpty()) {
                    _uiState.value = IntroUiState.NoValidToken
                    _uiEvent.emit(IntroUiEvent.ShowToast("로그인이 필요합니다"))
                    return@launch
                }

                checkValid()

            } catch (e: Exception) {
                _uiState.value = IntroUiState.NoValidToken
                _uiEvent.emit(IntroUiEvent.ShowToast("오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    private fun checkValid() {
        viewModelScope.launch {
            getIsAccessTokenValidUseCase()
                .collect {
                    if (it.result == true) { //토큰이 있고 유효함
                        _uiState.value = IntroUiState.Success
                    } else { //토큰이 있어도 유효하지 않음
                        _uiState.value = IntroUiState.NoValidToken
                        _uiEvent.emit(IntroUiEvent.ShowToast("로그인이 필요합니다"))
                    }
                }
        }
    }
}

sealed class IntroUiState {
    object Loading : IntroUiState()
    object Success : IntroUiState()
    object NoValidToken : IntroUiState()
}

sealed class IntroUiEvent {
    data class ShowToast(val error: String) : IntroUiEvent()
}