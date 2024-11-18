package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.response.TokenResponse
import com.eatssu.android.domain.repository.OauthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReissueTokenUseCase @Inject constructor(
    private val oauthRepository: OauthRepository,
) {
    suspend operator fun invoke(refreshToken: String): Flow<BaseResponse<TokenResponse>> =
        oauthRepository.reissueToken(refreshToken)
}