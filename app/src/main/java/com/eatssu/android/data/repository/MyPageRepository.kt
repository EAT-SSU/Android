package com.eatssu.android.data.repository

import com.eatssu.android.data.model.response.GetMyInfoResponseDto
import retrofit2.Callback


interface MyPageRepository {

    fun myInfoCheck(callback: Callback<GetMyInfoResponseDto>)
}