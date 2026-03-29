package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.domain.repository.OauthRepository
import javax.inject.Inject

class GetIsAccessTokenValidUseCase @Inject constructor(
    private val oauthRepository: OauthRepository
) {
    suspend operator fun invoke(userAccessToken: String): Boolean =
        oauthRepository.checkValidToken(userAccessToken)
}
