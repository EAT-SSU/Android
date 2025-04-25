package com.eatssu.android.domain.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class TokenState {
    VALID, EXPIRED
}

object TokenStateManager {
    private val _state = MutableStateFlow(TokenState.VALID)
    val state: StateFlow<TokenState> = _state

    fun setExpired() {
        _state.value = TokenState.EXPIRED
    }

    fun reset() {
        _state.value = TokenState.VALID
    }
}
