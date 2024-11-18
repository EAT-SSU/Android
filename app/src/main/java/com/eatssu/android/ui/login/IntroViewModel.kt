package com.eatssu.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<IntroState> = MutableStateFlow(IntroState())
    val uiState: StateFlow<IntroState> = _uiState.asStateFlow()

    init {
        autoLogin()
    }

    fun autoLogin() {
        viewModelScope.launch {
            if (getAccessTokenUseCase().isEmpty()) {
                _uiState.update { it.copy(isAutoLogined = false) }
            } else {
                _uiState.update { it.copy(isAutoLogined = true) }
            }
        }
    }

}

data class IntroState(
    var toastMessage: String = "",
    var loading: Boolean = true,
    var error: Boolean = false,
    var isAutoLogined: Boolean = false,
)