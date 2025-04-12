package com.eatssu.android.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetIsAccessTokenValidUseCase
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
class IntroViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getIsAccessTokenValidUseCase: GetIsAccessTokenValidUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState<IntroState>> = MutableStateFlow(UiState.Init)
    val uiState: StateFlow<UiState<IntroState>> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        autoLogin()
    }

    private fun autoLogin() {
        viewModelScope.launch {
            val userAccessToken = getAccessTokenUseCase()

            _uiState.value = UiState.Loading
            try {
                // 토큰 존재 여부 확인
                if (userAccessToken.isEmpty()) {
                    _uiState.value = UiState.Error
                    _uiEvent.emit(UiEvent.ShowToast("로그인이 필요합니다"))
                    return@launch
                } else {
                    checkValid(userAccessToken)
                }

            } catch (e: Exception) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    private fun checkValid(userAccessToken: String) {
        viewModelScope.launch {
            getIsAccessTokenValidUseCase(userAccessToken)
                .collect {
                    if (it.result == true) { //토큰이 있고 유효함
                        _uiState.value = UiState.Success(IntroState.ValidToken)
                    } else { //토큰이 있어도 유효하지 않음
                        _uiState.value = UiState.Error
                        _uiEvent.emit(UiEvent.ShowToast("로그인이 필요합니다"))
                    }
                }
        }
    }
}

sealed class IntroState {
    object ValidToken : IntroState()
}
