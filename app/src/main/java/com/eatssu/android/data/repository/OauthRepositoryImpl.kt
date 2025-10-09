package com.eatssu.android.data.repository

import com.eatssu.android.data.dto.request.CheckValidTokenRequest
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.dto.response.toDomain
import com.eatssu.android.data.service.OauthService
import com.eatssu.android.domain.model.Token
import com.eatssu.android.domain.repository.OauthRepository
import javax.inject.Inject

class OauthRepositoryImpl @Inject constructor(private val oauthService: OauthService) :
    OauthRepository {
    override suspend fun reissueToken(refreshToken: String): Token? =
        oauthService.getNewToken(refreshToken).map { it.toDomain() }.orNull()

    override suspend fun login(body: LoginWithKakaoRequest): Token? =
        oauthService.loginWithKakao(body).map { it.toDomain() }.orNull()

    override suspend fun checkValidToken(body: CheckValidTokenRequest): Boolean =
        oauthService.checkValidToken(body).orElse(false)
}
