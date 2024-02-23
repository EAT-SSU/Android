package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.dto.response.TokenResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OauthService {
    @POST("oauths/reissue/token") //accessToken, refreshToken 재발급
    fun getNewToken(
        @Header("Authorization") refreshToken: String?)
            : Call<BaseResponse<TokenResponseDto>>
    @POST("oauths/kakao")
    fun loginWithKakao(
        @Body request : LoginWithKakaoRequestDto)
            : Call<BaseResponse<TokenResponseDto>>
}