package com.eatssu.android.domain.repository

import com.eatssu.android.data.remote.dto.request.CheckValidTokenRequest
import com.eatssu.android.data.remote.dto.request.LoginWithKakaoRequest
import com.eatssu.android.domain.model.Token

interface OauthRepository {
    suspend fun reissueToken(
        refreshToken: String,
    ): Token?

    suspend fun login(body: LoginWithKakaoRequest): Token?

    suspend fun checkValidToken(body: CheckValidTokenRequest): Boolean
}

