package com.eatssu.android.data.service

import com.eatssu.android.data.model.request.ChangeNicknameRequestDto
import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.TokenResponseDto
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @POST("user/token/reissue") //accessToken, refreshToken 재발급
    fun getNewToken(
        @Header("Authorization") refreshToken: String?)
    : Call<TokenResponseDto>

    @PATCH("users/nickname") //닉네임 수정
    fun changeNickname(@Body request: ChangeNicknameRequestDto): Call<BaseResponse<Void>>

    @DELETE("users") //유저 탈퇴
    fun signOut(): Call<BaseResponse<String>>

    @GET("users/validate/nickname") //닉네임 중 복 체크, 존재하는 닉네임이면 errorCode 2012
    fun checkNickname(
        @Query("nickname") nickname: String
    ): Call<BaseResponse<String>>

}