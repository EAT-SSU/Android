package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.domain.model.Token
import com.eatssu.android.domain.repository.OauthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val oauthRepository: OauthRepository,
) {
    suspend operator fun invoke(body: LoginWithKakaoRequest): Token? =
        oauthRepository.login(body)
}