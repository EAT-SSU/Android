package com.eatssu.android.presentation.base

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.eatssu.android.domain.model.TokenState
import com.eatssu.android.domain.model.TokenStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor() : ViewModel() {

    private val _tokenExpiredEvent = MutableStateFlow(false)
    val tokenExpiredEvent: StateFlow<Boolean> = _tokenExpiredEvent

    @HiltViewModel
    class TokenViewModel @Inject constructor() : ViewModel() {

        private val _tokenExpiredEvent = MutableSharedFlow<Unit>(replay = 0)
        val tokenExpiredEvent = _tokenExpiredEvent

        init {
            viewModelScope.launch {
                TokenStateManager.state.collect { state ->
                    if (state == TokenState.EXPIRED) {
                        _tokenExpiredEvent.emit(Unit) // emit 1회성 이벤트
                    }
                }
            }
        }
    }

    fun notifyTokenExpired() {
        _tokenExpiredEvent.value = true
        Log.d("TokenViewModel", "Token expired event triggered")
    }

    fun clearTokenExpiredState() {
        _tokenExpiredEvent.value = false
        Log.d("TokenViewModel", "Token expired event reset")
    }
}
