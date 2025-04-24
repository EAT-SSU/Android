package com.eatssu.android.presentation.base

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor() : ViewModel() {

    private val _tokenExpiredEvent = MutableStateFlow(false)
    val tokenExpiredEvent: StateFlow<Boolean> = _tokenExpiredEvent

    fun notifyTokenExpired() {
        _tokenExpiredEvent.value = true
        Log.d("TokenViewModel", "Token expired event triggered")
    }

    fun resetTokenExpired() {
        _tokenExpiredEvent.value = false
        Log.d("TokenViewModel", "Token expired event reset")
    }
}
