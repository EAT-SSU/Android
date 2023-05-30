package com.eatssu.android.data.service

import com.eatssu.android.data.model.response.GetMyReviewResponse
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface MyPageService {

    @POST("mypage/myreview") //내가 쓴 리뷰 모아보기
    fun getMyReviews(@Query("lastReviewId") lastReviewId: Int): Call<GetMyReviewResponse>

    @POST("mypage/myreview") //내가 쓴 리뷰 모아보기
    fun getMyReviews(): Call<GetMyReviewResponse>
}