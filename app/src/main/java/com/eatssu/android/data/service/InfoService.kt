package com.eatssu.android.data.service

import com.eatssu.android.data.model.response.InfoResponse
import com.eatssu.android.data.model.response.MenuBaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InfoService {
    @GET("/restaurants/{restaurantName}")
    fun getRestaurantInfo(@Path("restaurantName") date: String)
            : Call<InfoResponse>
}