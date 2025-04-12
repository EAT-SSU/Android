package com.eatssu.android.domain.usecase.auth

import com.eatssu.android.data.dto.request.CheckValidTokenRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.domain.repository.OauthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIsAccessTokenValidUseCase @Inject constructor(
    private val oauthRepository: OauthRepository
) {
    suspend operator fun invoke(userAccessToken: String): Flow<BaseResponse<Boolean>> =
        oauthRepository.checkValidToken(CheckValidTokenRequest(userAccessToken))
}