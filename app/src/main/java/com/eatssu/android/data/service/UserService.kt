package com.eatssu.android.data.service

import ChangePwDto
import com.eatssu.android.data.model.request.LoginRequest
import com.eatssu.android.data.model.request.SignInRequest
import com.eatssu.android.data.model.request.TokenRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserService {
//    //이용약관수락
//    @POST("api/accounts/terms")
//    fun termsOk(@Body request : LoginDto) : Call<Token>

//
//    //이메일 중복 체크
//    @POST("user/user-emails/{email}/exist")
//
//
//    @POST("user/token/reissue")
//    //accessToken, refreshToken 재발급
//

    //로그인
    @POST("user/login")
    fun logIn(@Body request : LoginRequest) : Call<TokenRequest>


    //회원가입
    @POST("user/join")
    fun signIn(@Body request : SignInRequest) : Call<TokenRequest>

    //비밀번호 변경
    @PATCH("user/password")
    fun changePw(@Body requset : ChangePwDto) : Call<String>


//    @PATCH("user/nickname")
//    //닉네임 수정
}