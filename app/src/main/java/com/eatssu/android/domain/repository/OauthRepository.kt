package com.eatssu.android.domain.repository

import com.eatssu.android.data.dto.request.CheckValidTokenRequest
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.domain.model.Token

interface OauthRepository {
    suspend fun reissueToken(
        refreshToken: String,
    ): Token?

    suspend fun login(body: LoginWithKakaoRequest): ApiResult<Token>

    suspend fun checkValidToken(body: CheckValidTokenRequest): Boolean
}

