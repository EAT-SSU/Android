package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.domain.model.Token
import com.eatssu.android.domain.repository.OauthRepository
import javax.inject.Inject

class ReissueTokenUseCase @Inject constructor(
    private val oauthRepository: OauthRepository,
) {
    suspend operator fun invoke(refreshToken: String): Token? =
        oauthRepository.reissueToken(refreshToken)
}