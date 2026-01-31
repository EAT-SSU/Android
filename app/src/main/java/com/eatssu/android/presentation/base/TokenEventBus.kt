package com.eatssu.android.presentation.base

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class LogoutReason {
    MISSING_REFRESH_TOKEN,
    REFRESH_TOKEN_EXPIRED
}

/** application에서 발생하는 토큰 만료 이벤트를 전달하기 위한 Bus */
object TokenEventBus {
    private val _tokenExpired = MutableSharedFlow<LogoutReason>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val tokenExpired: SharedFlow<LogoutReason> = _tokenExpired.asSharedFlow()

    fun notifyTokenExpired(reason: LogoutReason) {
        _tokenExpired.tryEmit(reason)
    }
}
