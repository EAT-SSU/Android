package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.ChangeNicknameRequestDto
import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.TokenResponseDto
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    //accessToken, refreshToken 재발급
    @POST("user/token/reissue")
    fun getNewToken(
        @Header("Authorization") refreshToken: String?)
    : Call<TokenResponseDto>

    @PATCH("user/nickname") //닉네임 수정
    fun changeNickname(@Body request: ChangeNicknameRequestDto): Call<Void>

    @DELETE("user/signout") //유저 탈퇴
    fun signOut(): Call<BaseResponse>
}