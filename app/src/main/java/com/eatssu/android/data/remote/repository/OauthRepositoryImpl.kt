package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orElse
import com.eatssu.android.data.model.orNull
import com.eatssu.android.data.remote.dto.request.CheckValidTokenRequest
import com.eatssu.android.data.remote.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.OauthService
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
