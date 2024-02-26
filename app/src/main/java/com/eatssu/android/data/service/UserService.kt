package com.eatssu.android.data.service

import com.eatssu.android.base.BaseResponse
import com.eatssu.android.data.dto.request.ChangeNicknameRequestDto
import com.eatssu.android.data.dto.response.GetMyInfoResponseDto
import com.eatssu.android.data.dto.response.GetMyReviewResponseDto
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    @POST("users/validate/email/{email}") //이메일 중복 체크
    fun checkEmail(
        @Path("email") email: String,
    ): Call<BaseResponse<Boolean>>

    @PATCH("users/nickname") //닉네임 수정
    fun changeNickname(@Body request: ChangeNicknameRequestDto)
            : Call<BaseResponse<Void>>

    @GET("/users/validate/nickname") //닉네임 중복 체크
    fun checkNickname(
        @Query("nickname") nickname: String,
    )
            : Call<BaseResponse<Boolean>>

    @GET("users/reviews") //내가 쓴 리뷰 모아보기
    fun getMyReviews(): Call<BaseResponse<GetMyReviewResponseDto>> //Todo 언닝 해줭

    @GET("users/mypage") //내 정보 모아보기
    fun getMyInfo(): Call<BaseResponse<GetMyInfoResponseDto>>

    @DELETE("users") //유저 탈퇴
    fun signOut(): Call<BaseResponse<Boolean>>


}