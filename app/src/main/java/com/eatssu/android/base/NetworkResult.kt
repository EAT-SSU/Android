package com.eatssu.android.base

sealed class NetworkResult<T>(var data: Any? = null, val message: String? = null) {
    data class Success<T> constructor(val value: T) : NetworkResult<T>(data = value)
    class Error<T> @JvmOverloads constructor(
        var code: Int? = null,
        var msg: String? = null,
        var exception: Throwable? = null
    ) : NetworkResult<T>(code, msg)

    class Loading<T> : NetworkResult<T>()
}