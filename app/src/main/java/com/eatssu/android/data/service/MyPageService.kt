package com.eatssu.android.data.service

import com.eatssu.android.data.model.response.BaseResponse
import com.eatssu.android.data.model.response.GetMyInfoResponseDto
import com.eatssu.android.data.model.response.GetMyReviewResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MyPageService {
    @GET("users/reviews") //내가 쓴 리뷰 모아보기
    fun getMyReviews(): Call<BaseResponse<GetMyReviewResponseDto>>

    @GET("users/mypage") //내 정보 모아보기
    fun getMyInfo(): Call<BaseResponse<GetMyInfoResponseDto>>
}