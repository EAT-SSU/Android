package com.eatssu.android.data.service

import com.eatssu.android.data.dto.response.InfoResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface InfoService {
    @GET("/restaurants/{restaurantName}")
    fun getRestaurantInfo(@Path("restaurantName") date: String)
            : Call<InfoResponseDto>
}