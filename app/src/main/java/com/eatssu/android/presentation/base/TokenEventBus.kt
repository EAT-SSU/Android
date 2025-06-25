package com.eatssu.android.presentation.base

import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber

/** application에서 발생하는 토큰 만료 이벤트를 전달하기 위한 Bus */
object TokenEventBus {
    private val _tokenExpired = MutableSharedFlow<Unit>(replay = 0)
    val tokenExpired = _tokenExpired

    private val _tokenServerError = MutableSharedFlow<Unit>(replay = 0)
    val tokenServerError = _tokenServerError

    suspend fun notifyTokenExpired() {
        _tokenExpired.emit(Unit)
    }

    suspend fun notifyServerError() {
        _tokenServerError.emit(Unit)
    }
}
