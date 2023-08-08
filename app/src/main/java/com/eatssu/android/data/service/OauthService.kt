package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.loginWithKakaoRequest
import com.eatssu.android.data.model.response.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OauthService {

    @POST("oauth/kakao")
    fun loginWithKakao( @Body request : loginWithKakaoRequest) : Call<TokenResponse>
}