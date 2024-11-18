package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.TokenResponse
import com.eatssu.android.domain.repository.OauthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val oauthRepository: OauthRepository,
) {
    suspend operator fun invoke(body: LoginWithKakaoRequest): Flow<BaseResponse<TokenResponse>> =
        oauthRepository.login(body)
}