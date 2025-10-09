package com.eatssu.android.data.model

import java.io.IOException

sealed class ApiResult<T : Any> {
    fun <R : Any> map(transform: (T) -> R): ApiResult<out R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> Failure(responseCode, message)
        is NetworkError -> NetworkError(exception)
        is UnknownError -> UnknownError(exception)
    }

    fun orElse(default: T): T = when (this) {
        is Success -> data
        else -> default
    }

    fun orElse(default: () -> T): T = when (this) {
        is Success -> data
        else -> default()
    }

    fun orNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    data class Success<T : Any>(val data: T) : ApiResult<T>()

    data class Failure(
        val responseCode: Int,
        val message: String?
    ) : ApiResult<Nothing>()

    data class NetworkError(
        val exception: IOException
    ) : ApiResult<Nothing>()

    data class UnknownError(
        val exception: Throwable
    ) : ApiResult<Nothing>()

}

fun <TElement, TList : List<TElement>> ApiResult<TList>.orEmptyList(): List<TElement> =
    when (this) {
        is ApiResult.Success -> data
        else -> emptyList()
    }

fun ApiResult<Unit>.isSuccess(): Boolean = this is ApiResult.Success