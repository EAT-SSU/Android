package com.eatssu.android.ui.main

import com.eatssu.android.data.model.Haksik
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitInterface {
    @GET("/menu/{date}/morning")
    fun requestAllData() : Call<Haksik>

}