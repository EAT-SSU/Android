package com.eatssu.android.domain.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.dto.response.TokenResponse
import kotlinx.coroutines.flow.Flow

interface OauthRepository {
    suspend fun reissueToken(
        refreshToken: String,
    ): Flow<BaseResponse<TokenResponse>>

    suspend fun login(body: LoginWithKakaoRequest): Flow<BaseResponse<TokenResponse>>

}

