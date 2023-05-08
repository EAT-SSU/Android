package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.ChangeNickname
import com.eatssu.android.data.model.request.ChangePwDto
import com.eatssu.android.data.model.request.LoginRequest
import com.eatssu.android.data.model.request.SignUpRequest
import com.eatssu.android.data.model.response.TokenResponse
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    //이메일 중복 체크
    @POST("user/user-emails/{email}/exist")
    fun getEmailExist(@Path ("email") email : String) :Call<Boolean>


    //accessToken, refreshToken 재발급
    @POST("user/token/reissue")
    fun getNewToken() :Call<TokenResponse>


    //로그인
    @POST("user/login")
    fun logIn(@Body request : LoginRequest) : Call<TokenResponse>

    //회원가입
    @POST("user/join")
    fun signUp(@Body request : SignUpRequest) : Call<TokenResponse>

    //비밀번호 변경
    @PATCH("user/password")
    fun changePw(@Body request : ChangePwDto) : Call<String>

    //닉네임 수정
    @PATCH("user/nickname")
    fun changeNickname(@Body request: ChangeNickname) :Call<String>
}