package com.eatssu.android.domain.repository

import com.eatssu.android.domain.model.AppTheme
import com.eatssu.android.domain.model.RestaurantInfo
import com.eatssu.common.enums.Restaurant

interface FirebaseRemoteConfigRepository {

    /**
     * 앱의 최신 버전 코드 반환
     * 값을 가져오기 전에 fetchAndActivate를 호출하여 최신 값을 가져옵니다.
     */
    suspend fun getMinimumVersionCode(): Long

    /**
     * 앱 테마를 Remote Config에서 가져옵니다.
     */
    suspend fun getAppTheme(): AppTheme

    /**
     * 특정 식당 정보를 Remote Config에서 가져옴
     * 값을 가져오기 전에 fetchAndActivate를 호출하여 최신 값을 가져옵니다.
     */
    suspend fun getRestaurantInfo(restaurant: Restaurant): RestaurantInfo?
}
