package com.eatssu.android.data.service

import com.eatssu.android.data.model.response.MenuBaseResponse
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