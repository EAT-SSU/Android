package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.LoginWithKakaoRequest
import com.eatssu.android.data.dto.response.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OauthService { //여기는 토큰이 없는 레트로핏을 끼웁니다.
    @POST("oauths/reissue/token") //accessToken, refreshToken 재발급
    fun getNewToken(
        @Header("Authorization") refreshToken: String?,
    ) //얘는 SP에 있는거 헤더에 넣어주면 됩니다.
            : Call<BaseResponse<TokenResponse>>

    @POST("oauths/kakao")
    suspend fun loginWithKakao(
        @Body request: LoginWithKakaoRequest,
    ): BaseResponse<TokenResponse>
}