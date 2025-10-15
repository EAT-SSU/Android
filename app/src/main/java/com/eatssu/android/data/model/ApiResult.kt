package com.eatssu.android.data.model

import com.eatssu.android.data.model.ApiResult.Failure
import com.eatssu.android.data.model.ApiResult.NetworkError
import com.eatssu.android.data.model.ApiResult.Success
import com.eatssu.android.data.model.ApiResult.UnknownError
import java.io.IOException

sealed interface ApiResult<out T> {
    // API가 성공했을 때
    data class Success<T>(val data: T) : ApiResult<T>

    // 서버에서 에러 반환
    data class Failure(
        val responseCode: Int,
        val message: String?
    ) : ApiResult<Nothing>

    // 소켓 연결 끊김, 타임아웃 등 네트워크 예외인 IOException 처리
    data class NetworkError(
        val exception: IOException
    ) : ApiResult<Nothing>

    // IOException을 제외한 예외 처리
    data class UnknownError(
        val exception: Throwable
    ) : ApiResult<Nothing>
}

// ApiResult가 성공인지 아닌지 여부 확인
fun ApiResult<Unit>.isSuccess(): Boolean = this is Success

// 성공한 경우 데이터 반환, 실패한 경우 빈 리스트 반환
fun <TElement, TList : List<TElement>> ApiResult<TList>.orEmptyList(): List<TElement> =
    when (this) {
        is Success -> data
        else -> emptyList()
    }

// 성공한 경우 데이터 반환, 실패한 경우 기본값 반환
fun <T> ApiResult<T>.orElse(default: T): T = when (this) {
    is Success -> data
    else -> default
}

// 성공한 경우 데이터 반환, 실패한 경우 null 반환
fun <T> ApiResult<T>.orNull(): T? = when (this) {
    is Success -> data
    else -> null
}

// ApiResult가 Succes일 때 데이터를 변환
fun <T, R> ApiResult<T>.map(transform: (T) -> R): ApiResult<R> = when (this) {
    is Success<T> -> Success(transform(data))
    is Failure -> Failure(responseCode, message)
    is NetworkError -> NetworkError(exception)
    is UnknownError -> UnknownError(exception)
}