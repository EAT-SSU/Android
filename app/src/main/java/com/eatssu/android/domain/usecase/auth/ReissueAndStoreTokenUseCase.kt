package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.model.ApiResult
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
            is ApiResult.Success -> {
                val newAccessToken = result.data.accessToken
                val newRefreshToken = result.data.refreshToken

                if (newAccessToken.isBlank() || newRefreshToken.isBlank()) {
                    ReissueAndStoreResult.TransientFailure(message = "reissue returned blank tokens")
                } else {
                    setAccessTokenUseCase(newAccessToken)
                    setRefreshTokenUseCase(newRefreshToken)
                    ReissueAndStoreResult.Success(accessToken = newAccessToken)
                }
            }

            is ApiResult.Failure -> {
                if (result.responseCode == 401 || result.responseCode == 403) {
                    ReissueAndStoreResult.RefreshInvalid(result.responseCode, result.message)
                } else {
                    ReissueAndStoreResult.TransientFailure(
                        responseCode = result.responseCode,
                        message = result.message,
                    )
                }
            }

            is ApiResult.NetworkError -> ReissueAndStoreResult.TransientFailure(throwable = result.exception)
            is ApiResult.UnknownError -> ReissueAndStoreResult.TransientFailure(throwable = result.exception)
        }
    }
}
