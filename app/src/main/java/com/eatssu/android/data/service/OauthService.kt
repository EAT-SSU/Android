package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.LoginWithKakaoRequestDto
import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.TokenResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OauthService {

    @POST("oauths/kakao")
    fun loginWithKakao( @Body request : LoginWithKakaoRequestDto) : Call<BaseResponse<TokenResponseDto>>
}