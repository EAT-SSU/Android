package com.eatssu.android.presentation.intro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eatssu.android.R
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.usecase.auth.GetAccessTokenUseCase
import com.eatssu.android.domain.usecase.auth.GetIsAccessTokenValidUseCase
import com.eatssu.android.domain.usecase.health.CheckServerHealthUseCase
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getIsAccessTokenValidUseCase: GetIsAccessTokenValidUseCase,
    private val checkServerHealthUseCase: CheckServerHealthUseCase,
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

            when (checkServerHealthUseCase()) {
                is ApiResult.NetworkError -> {
                    Timber.e("Network Error")
                    _uiEvent.emit(
                        UiEvent.NavigateToServerError(
                            context.getString(R.string.server_error_title),
                            context.getString(R.string.server_error_message)
                        )
                    )
                    return@launch
                }

                else -> Unit
            }

            val userAccessToken = getAccessTokenUseCase()

            try {
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
            if (getIsAccessTokenValidUseCase(userAccessToken)) { //토큰이 있고 유효함
                _uiState.value = UiState.Success(IntroState.ValidToken)
            } else { //토큰이 있어도 유효하지 않음
                _uiState.value = UiState.Error
                _uiEvent.emit(UiEvent.ShowToast("로그인이 필요합니다"))
            }
        }
    }
}

sealed class IntroState {
    object ValidToken : IntroState()
}
