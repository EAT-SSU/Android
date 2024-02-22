package com.eatssu.android.data.repository

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.base.NetworkResult
import com.eatssu.android.data.dto.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.dto.response.TokenResponseDto
import com.eatssu.android.data.service.OauthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import javax.inject.Inject


class OauthRepositoryImpl @Inject constructor(private val oauthService: OauthService) :
    OauthRepository {
    override suspend fun kakaoLogin(requestDto : LoginWithKakaoRequestDto): Flow<BaseResponse<TokenResponseDto>> = flow {
        emit(oauthService.loginWithKakao(requestDto))
    }

//    override suspend fun kakaoLogin(requestDto : LoginWithKakaoRequestDto) = flow {
//        return oauthService.loginWithKakao(requestDto)
    
//        emit(oauthService.loginWithKakao(requestDto))
//    }

    override suspend fun getNewToken(refreshToken: String): Flow<BaseResponse<TokenResponseDto>> =flow {
        emit(oauthService.getNewToken(refreshToken))
    }


}
