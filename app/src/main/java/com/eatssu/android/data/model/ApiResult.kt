package com.eatssu.android.data.model

import java.io.IOException

sealed interface ApiResult<out T> {
    fun <R : Any> map(transform: (T) -> R): ApiResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> Failure(responseCode, message)
        is NetworkError -> NetworkError(exception)
        is UnknownError -> UnknownError(exception)
    }

    // API가 성공했을 때
    data class Success<T : Any>(val data: T) : ApiResult<T>

    // 서버에서 에러 반환
    data class Failure(
        val responseCode: Int,
        val message: String?
    ) : ApiResult<Nothing>

    // 소켓 연결 끊김, 타임아웃 등 네트워크 예외인 IOException을 처리
    data class NetworkError(
        val exception: IOException
    ) : ApiResult<Nothing>

    // IOException을 제외한 기타 예외 처리
    data class UnknownError(
        val exception: Throwable
    ) : ApiResult<Nothing>

}

fun <TElement, TList : List<TElement>> ApiResult<TList>.orEmptyList(): List<TElement> =
    when (this) {
        is ApiResult.Success -> data
        else -> emptyList()
    }

fun ApiResult<Unit>.isSuccess(): Boolean = this is ApiResult.Success

fun <T> ApiResult<T>.orElse(default: T): T = when (this) {
    is ApiResult.Success -> data
    else -> default
}

fun <T> ApiResult<T>.orNull(): T? = when (this) {
    is ApiResult.Success -> data
    else -> null
}