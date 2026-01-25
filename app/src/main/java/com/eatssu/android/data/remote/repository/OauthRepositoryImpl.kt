package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.model.map
import com.eatssu.android.data.model.orElse
import com.eatssu.android.data.model.orNull
import com.eatssu.android.data.remote.dto.request.CheckValidTokenRequest
import com.eatssu.android.data.remote.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.remote.dto.response.toDomain
import com.eatssu.android.data.remote.service.OauthService
import com.eatssu.android.domain.model.Token
import com.eatssu.android.domain.repository.OauthRepository
import com.eatssu.common.enums.DeviceType
import javax.inject.Inject

class OauthRepositoryImpl @Inject constructor(private val oauthService: OauthService) :
    OauthRepository {
    override suspend fun reissueToken(refreshToken: String): ApiResult<Token> {
        val headerValue = refreshToken.asAuthorizationHeaderValue()
        return oauthService.getNewToken(headerValue).map { it.toDomain() }
    }

    override suspend fun login(
        email: String,
        providerId: String,
        deviceType: DeviceType,
    ): Token? =
        oauthService.loginWithKakao(
            LoginWithKakaoRequest(
                email = email,
                providerId = providerId,
                deviceType = deviceType,
            )
        ).map { it.toDomain() }.orNull()

    override suspend fun checkValidToken(body: CheckValidTokenRequest): Boolean =
        oauthService.checkValidToken(body).orElse(false)
}

private fun String.asAuthorizationHeaderValue(): String =
    if (startsWith("Bearer ")) this else "Bearer $this"
