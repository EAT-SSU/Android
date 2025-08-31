package com.eatssu.android.domain.model

sealed class Result {
    object Success : Result()
    data class Failure(val message: String) : Result()
} 