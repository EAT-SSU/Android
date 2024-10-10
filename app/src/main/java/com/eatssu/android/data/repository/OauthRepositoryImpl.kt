package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.dto.response.TokenResponse
import com.eatssu.android.domain.repository.OauthRepository
import com.eatssu.android.domain.service.OauthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OauthRepositoryImpl @Inject constructor(private val oauthService: OauthService) :
    OauthRepository {
    override suspend fun reissueToken(refreshToken: String): Flow<BaseResponse<TokenResponse>> =
        flow {
            emit(oauthService.getNewToken(refreshToken))
        }


    override suspend fun login(body: LoginWithKakaoRequest): Flow<BaseResponse<TokenResponse>> =
        flow {
            emit(oauthService.loginWithKakao(body))
        }
}
