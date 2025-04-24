package com.eatssu.android.presentation.base

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** 토큰 만료 시 알림을 위한 싱글톤 객체 */
object TokenExpiredNotifier {
    private val _tokenExpired = MutableStateFlow(false)
    val tokenExpired: StateFlow<Boolean> = _tokenExpired

    fun tokenExpired() {
        _tokenExpired.value = true
    }

    fun resetTokenState() {
        _tokenExpired.value = false
    }
}

@HiltViewModel
class TokenViewModel @Inject constructor() : ViewModel() {

    private val _tokenExpiredEvent = MutableStateFlow(false)
    val tokenExpiredEvent: StateFlow<Boolean> = _tokenExpiredEvent

    fun notifyTokenExpired() {
        _tokenExpiredEvent.value = true
    }

    fun resetTokenExpired() {
        _tokenExpiredEvent.value = false
    }
}
