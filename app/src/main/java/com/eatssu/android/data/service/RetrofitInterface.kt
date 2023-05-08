package com.eatssu.android.data.service

import com.eatssu.android.data.model.Haksik
import com.eatssu.android.data.model.request.LoginRequest
import com.eatssu.android.data.model.response.MenuBaseResponse
import com.eatssu.android.data.model.response.TokenResponse
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {
    @GET("/menu/{date}/morning")
    fun getMenuMorning(@Query("restaurant") restaurant : String,
    @Path("date") date: String)
    : Call<MenuBaseResponse>

    @GET("/menu/{date}/lunch")
    fun getMenuLunch(@Query("restaurant") restaurant : String,
                       @Path("date") date: String)
            : Call<MenuBaseResponse>

    @GET("/menu/{date}/dinner")
    fun getMenuDinner(@Query("restaurant") restaurant : String,
                       @Path("date") date: String)
            : Call<MenuBaseResponse>




}