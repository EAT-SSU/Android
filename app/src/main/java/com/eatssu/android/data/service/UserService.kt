package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.ChangeNicknameRequestDto
import com.eatssu.android.data.model.request.LoginRequestDto
import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.TokenResponseDto
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    //이메일 중복 체크
    @POST("user/user-emails/{email}/exist")
    fun getEmailExist(@Path("email") email: String): Call<Boolean>

    //accessToken, refreshToken 재발급
    @POST("user/token/reissue")
    fun getNewToken(): Call<TokenResponseDto>

    //로그인
    @POST("user/login")
    fun logIn(@Body request: LoginRequestDto): Call<TokenResponseDto>

    //닉네임 수정
    @PATCH("user/nickname")
    fun changeNickname(@Body request: ChangeNicknameRequestDto): Call<Void>

    @DELETE("user/signout")
    fun signOut(): Call<BaseResponse>
}