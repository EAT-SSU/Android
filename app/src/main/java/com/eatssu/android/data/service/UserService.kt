package com.eatssu.android.data.service

import com.eatssu.android.data.dto.request.ChangeNicknameRequest
import com.eatssu.android.data.dto.response.BaseResponse
import com.eatssu.android.data.dto.response.MyInfoResponse
import com.eatssu.android.data.dto.response.MyReviewResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface UserService {

    @PATCH("users/nickname") //닉네임 수정
    suspend fun changeNickname(
        @Body request: ChangeNicknameRequest,
    ): BaseResponse<Void>

    @GET("users/validate/nickname") //닉네임 중복 체크
    suspend fun checkNickname(
        @Query("nickname") nickname: String,
    ): BaseResponse<Boolean>

    @GET("users/reviews") //내가 쓴 리뷰 모아보기
    suspend fun getMyReviews(): BaseResponse<MyReviewResponse>

    @GET("users/mypage") //내 정보 모아보기
    suspend fun getMyInfo(): BaseResponse<MyInfoResponse>

    @DELETE("users") //유저 탈퇴
    suspend fun signOut(): BaseResponse<Boolean>


}