package com.eatssu.android.ui.main

import com.eatssu.android.data.model.Haksik
import com.eatssu.android.data.model.request.LoginRequest
import com.eatssu.android.data.model.response.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitInterface {
    @GET("/menu/{date}/morning")
    fun requestAllData() : Call<Haksik>

    @POST("user/login")
    fun logIn(@Body request : LoginRequest) : Call<TokenResponse>


}