package com.eatssu.android.domain.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

enum class TokenState {
    INITIAL, VALID, EXPIRED, ERROR
}

/** 현재 토큰 상태를 관리하는 객체 */
object TokenStateManager {
    private val _state = MutableStateFlow(TokenState.INITIAL)
    val state: StateFlow<TokenState> = _state

    fun setTokenExpired() {
        _state.value = TokenState.EXPIRED
        Timber.e("TokenStateManager → Token expired")
    }

    fun setTokenValid() {
        _state.value = TokenState.VALID
        Timber.d("TokenStateManager → Token valid")
    }

    fun setTokenError() {
        _state.value = TokenState.ERROR
        Timber.e("TokenStateManager → Token error")
    }
}

