package com.eatssu.android.data.remote.service

import com.eatssu.android.data.remote.dto.response.KakaoLocalSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoLocalService {
    @GET("v2/local/search/keyword.json")
    suspend fun searchPlaces(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("x") longitude: Double,
        @Query("y") latitude: Double,
        @Query("radius") radius: Int = 300,
        @Query("size") size: Int = 5,
        @Query("sort") sort: String = "distance",
    ): Response<KakaoLocalSearchResponse>
}
