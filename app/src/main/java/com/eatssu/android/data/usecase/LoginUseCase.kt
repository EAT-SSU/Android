package com.eatssu.android.data.usecase

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.dto.response.TokenResponse
import com.eatssu.android.data.repository.OauthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val oauthRepository: OauthRepository,
) {
    suspend operator fun invoke(body: LoginWithKakaoRequest): Flow<BaseResponse<TokenResponse>> =
        oauthRepository.login(body)
}