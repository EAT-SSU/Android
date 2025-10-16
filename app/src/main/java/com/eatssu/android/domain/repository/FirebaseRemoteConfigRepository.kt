package com.eatssu.android.domain.repository

import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.common.enums.Restaurant

interface FirebaseRemoteConfigRepository {

    /**
     * Remote Config 초기화 및 fetch
     * @return 성공/실패 여부를 Result로 반환
     */
    suspend fun init(): Result<Unit>

    /**
     * 앱의 최신 버전 코드 반환
     */
    fun getMinimumVersionCode(): Long

    /**
     * 특정 식당 정보를 Remote Config에서 가져옴
     */
    fun getRestaurantInfo(restaurant: Restaurant): RestaurantInfo?
}
