package com.eatssu.android.domain.repository

import com.eatssu.android.data.remote.dto.request.CheckValidTokenRequest
import com.eatssu.android.domain.model.Token
import com.eatssu.common.enums.DeviceType

interface OauthRepository {
    suspend fun reissueToken(
        refreshToken: String,
    ): Token?

    suspend fun login(
        email: String,
        providerId: String,
        deviceType: DeviceType,
    ): Token?

    suspend fun checkValidToken(body: CheckValidTokenRequest): Boolean
}

