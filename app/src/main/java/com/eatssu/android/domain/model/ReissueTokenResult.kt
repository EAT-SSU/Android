package com.eatssu.android.domain.model

sealed interface ReissueTokenResult {
    data class Success(val token: Token) : ReissueTokenResult

    data class Failure(
        val responseCode: Int? = null,
        val message: String? = null,
        val throwable: Throwable? = null
    ) : ReissueTokenResult
}
