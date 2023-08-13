package com.eatssu.android.data.service

import com.eatssu.android.data.model.response.GetMyReviewResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MyPageService {
    @GET("mypage/myreview") //내가 쓴 리뷰 모아보기
    fun getMyReviews(): Call<GetMyReviewResponse>

    @GET("mypage/info") //내가 쓴 리뷰 모아보기
    fun getMyInfo(): Call<GetMyReviewResponse>
}