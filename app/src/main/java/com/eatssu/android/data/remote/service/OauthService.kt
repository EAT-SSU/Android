package com.eatssu.android.data.remote.service

import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.dto.request.CheckValidTokenRequest
import com.eatssu.android.data.remote.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.remote.dto.response.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OauthService { //여기는 토큰이 없는 레트로핏을 끼웁니다.
    @POST("oauths/reissue/token") //accessToken, refreshToken 재발급
    suspend fun getNewToken(
        @Header("Authorization") refreshToken: String?,
    ) //얘는 SP에 있는거 헤더에 넣어주면 됩니다.
            : ApiResult<TokenResponse>

    /**
     *  카카오 회원가입, 로그인 V2 [인증 토큰 필요 X]
     */
    @POST("oauths/v2/kakao")
    suspend fun loginWithKakao(
        @Body request: LoginWithKakaoRequest,
    ): ApiResult<TokenResponse>

    @POST("oauths/valid/token")
    suspend fun checkValidToken(
        @Body request: CheckValidTokenRequest,
    ): ApiResult<Boolean>
}