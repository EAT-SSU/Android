package com.eatssu.android.data.repository

import android.telecom.Call
import com.eatssu.android.base.BaseResponse
import com.eatssu.android.base.NetworkResult
import com.eatssu.android.data.dto.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.dto.response.TokenResponseDto
import kotlinx.coroutines.flow.Flow

interface OauthRepository {

//    suspend fun kakaoLogin(requestDto: LoginWithKakaoRequestDto): Flow<BaseResponse<TokenResponseDto>>
    suspend fun kakaoLogin(requestDto: LoginWithKakaoRequestDto)
    : Flow<BaseResponse<TokenResponseDto>>
    suspend fun getNewToken(refreshToken: String)
    : Flow<BaseResponse<TokenResponseDto>>

}