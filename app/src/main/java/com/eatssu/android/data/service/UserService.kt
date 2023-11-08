package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.ChangeNicknameRequestDto
import com.eatssu.android.data.model.request.ChangePwRequestDto
import com.eatssu.android.data.model.request.LoginRequestDto
import com.eatssu.android.data.model.request.SignUpRequestDto
import com.eatssu.android.data.model.response.TokenResponseDto
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    //이메일 중복 체크
    @POST("user/user-emails/{email}/exist")
    fun getEmailExist(@Path("email") email: String): Call<Boolean>

    //accessToken, refreshToken 재발급
    @POST("user/token/reissue")
    fun getNewToken(
        @Header("Authorization") refreshToken: String?)
    : Call<TokenResponseDto>

    //로그인
    @POST("user/login")
    fun logIn(@Body request: LoginRequestDto): Call<TokenResponseDto>

    //회원가입
    @POST("user/join")
    fun signUp(@Body request: SignUpRequestDto): Call<TokenResponseDto>

    //비밀번호 변경
    @PATCH("user/password")
    fun changePw(@Body request: ChangePwRequestDto): Call<String>

    //닉네임 수정
    @PATCH("user/nickname")
    fun changeNickname(@Body request: ChangeNicknameRequestDto): Call<Void>
}