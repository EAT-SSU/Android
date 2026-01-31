package com.eatssu.android.presentation.base

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import timber.log.Timber

/** application에서 발생하는 네트워크 에러 이벤트를 전달하기 위한 Bus */
object NetworkErrorEventBus {
    private val _networkError = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val networkError = _networkError

    fun notifyNetworkError() {
        Timber.e("NetworkErrorEventBus → Network error occurred")
        _networkError.tryEmit(Unit)
    }
}
