package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.domain.model.ReissueTokenResult
import javax.inject.Inject

sealed interface ReissueAndStoreResult {
    data class Success(
        val accessToken: String,
    ) : ReissueAndStoreResult

    data object MissingRefreshToken : ReissueAndStoreResult

    data class RefreshInvalid(
        val responseCode: Int,
        val message: String?,
    ) : ReissueAndStoreResult

    data class TransientFailure(
        val responseCode: Int? = null,
        val message: String? = null,
        val throwable: Throwable? = null,
    ) : ReissueAndStoreResult
}

class ReissueAndStoreTokenUseCase @Inject constructor(
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val reissueTokenUseCase: ReissueTokenUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
    private val setRefreshTokenUseCase: SetRefreshTokenUseCase,
) {
    suspend operator fun invoke(): ReissueAndStoreResult {
        val refreshToken = getRefreshTokenUseCase()
        if (refreshToken.isBlank()) return ReissueAndStoreResult.MissingRefreshToken

        return when (val result = reissueTokenUseCase(refreshToken)) {
            is ReissueTokenResult.Success -> {
                val newAccessToken = result.token.accessToken
                val newRefreshToken = result.token.refreshToken

                if (newAccessToken.isBlank() || newRefreshToken.isBlank()) {
                    ReissueAndStoreResult.TransientFailure(message = "reissue returned blank tokens")
                } else {
                    setAccessTokenUseCase(newAccessToken)
                    setRefreshTokenUseCase(newRefreshToken)
                    ReissueAndStoreResult.Success(accessToken = newAccessToken)
                }
            }

            is ReissueTokenResult.Failure -> {
                val code = result.responseCode
                if (code == 401 || code == 403) {
                    ReissueAndStoreResult.RefreshInvalid(code, result.message)
                } else {
                    ReissueAndStoreResult.TransientFailure(
                        responseCode = result.responseCode,
                        message = result.message,
                        throwable = result.throwable
                    )
                }
            }
        }
    }
}
