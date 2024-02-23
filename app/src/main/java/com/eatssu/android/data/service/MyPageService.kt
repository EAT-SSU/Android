package com.eatssu.android.data.service

import com.eatssu.android.data.dto.response.GetMyInfoResponseDto
import com.eatssu.android.data.dto.response.GetMyReviewResponseDto
import retrofit2.Call
import retrofit2.http.GET

interface MyPageService {
    @GET("mypage/myreview") //내가 쓴 리뷰 모아보기
    fun getMyReviews(): Call<GetMyReviewResponseDto>

    @GET("mypage/info") //내 정보 모아보기
    fun getMyInfo(): Call<GetMyInfoResponseDto>
}