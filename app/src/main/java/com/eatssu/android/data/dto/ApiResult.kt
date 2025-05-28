package com.eatssu.android.data.dto

sealed class ApiResult<T> {
    data class Success<T>(val data: T?) : ApiResult<T>()
    data class Failure(val message: String) : ApiResult<Nothing>()
}