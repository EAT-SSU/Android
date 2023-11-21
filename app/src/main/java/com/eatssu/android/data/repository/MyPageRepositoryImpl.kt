package com.eatssu.android.data.repository

import com.eatssu.android.data.model.response.GetMyInfoResponseDto
import com.eatssu.android.data.service.MyPageService
import retrofit2.Callback

class MyPageRepositoryImpl (
    private val myPageService: MyPageService
) : MyPageRepository {

    override fun myInfoCheck(callback: Callback<GetMyInfoResponseDto>) {
        myPageService.getMyInfo().enqueue(callback)
    }
}