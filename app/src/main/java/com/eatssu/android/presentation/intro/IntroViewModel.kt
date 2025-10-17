package com.eatssu.android.presentation.intro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetIsAccessTokenValidUseCase
import com.eatssu.android.presentation.UiEvent
import com.eatssu.android.presentation.UiState
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
class IntroViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getIsAccessTokenValidUseCase: GetIsAccessTokenValidUseCase,
    @ApplicationContext private val context: Context
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
            _uiState.value = UiState.Loading

            val userAccessToken = getAccessTokenUseCase()
            if (userAccessToken.isEmpty()) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("로그인이 필요합니다"))
                return@launch
            }

            // 토큰이 있어도 유효하지 않음
            if (!getIsAccessTokenValidUseCase(userAccessToken)) {
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("로그인이 필요합니다"))
                return@launch
            }

            // 토큰이 있고 유효함
            _uiState.value = UiState.Success(IntroState.ValidToken)

        }
    }
}

sealed class IntroState {
    object ValidToken : IntroState()
}
