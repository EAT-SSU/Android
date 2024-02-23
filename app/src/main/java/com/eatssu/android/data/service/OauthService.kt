package com.eatssu.android.data.service

import com.eatssu.android.data.dto.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.dto.response.TokenResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OauthService {

    @POST("oauth/kakao")
    fun loginWithKakao( @Body request : LoginWithKakaoRequestDto) : Call<TokenResponseDto>
}