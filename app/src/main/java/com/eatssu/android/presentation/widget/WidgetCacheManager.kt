package com.eatssu.android.presentation.widget


import android.os.Build
import androidx.annotation.RequiresApi
import com.eatssu.android.domain.model.WidgetMealInfo
import com.eatssu.common.enums.Restaurant
import timber.log.Timber
import java.time.LocalDateTime

/**
 * 식당별 위젯 데이터 캐싱을 관리하는 클래스
 * 각 식당의 메뉴 데이터를 캐싱하여 중복 API 호출을 방지합니다.
 */
object WidgetCacheManager {

    // 캐시 유효 시간 (분)
    private const val CACHE_VALIDITY_MINUTES = 30L

    // 식당별 캐시 데이터
    private val cacheMap = mutableMapOf<Restaurant, CachedMealData>()

    data class CachedMealData(
        val mealInfo: WidgetMealInfo,
        val timestamp: LocalDateTime,
        val date: String
    )

    /**
     * 캐시된 데이터가 유효한지 확인
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isCacheValid(cachedData: CachedMealData, currentDate: String): Boolean {
        val now = LocalDateTime.now()
        val timeDiff = java.time.Duration.between(cachedData.timestamp, now)

        return cachedData.date == currentDate &&
                timeDiff.toMinutes() < CACHE_VALIDITY_MINUTES
    }

    /**
     * 캐시에서 식당별 메뉴 데이터 조회
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCachedMealData(restaurant: Restaurant, currentDate: String): WidgetMealInfo? {
        val cachedData = cacheMap[restaurant] ?: return null

        return if (isCacheValid(cachedData, currentDate)) {
            Timber.d("Cache hit for ${restaurant.name} on $currentDate")
            cachedData.mealInfo
        } else {
            Timber.d("Cache expired for ${restaurant.name} on $currentDate")
            cacheMap.remove(restaurant)
            null
        }
    }

    /**
     * 식당별 메뉴 데이터를 캐시에 저장
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun cacheMealData(restaurant: Restaurant, mealInfo: WidgetMealInfo, date: String) {
        val cachedData = CachedMealData(
            mealInfo = mealInfo,
            timestamp = LocalDateTime.now(),
            date = date
        )

        cacheMap[restaurant] = cachedData
        Timber.d("Cached meal data for ${restaurant.name} on $date")
    }

    /**
     * 특정 식당의 캐시 데이터 삭제
     */
    fun clearCacheForRestaurant(restaurant: Restaurant) {
        cacheMap.remove(restaurant)
        Timber.d("Cleared cache for ${restaurant.name}")
    }

    /**
     * 모든 캐시 데이터 삭제
     */
    fun clearAllCache() {
        cacheMap.clear()
        Timber.d("Cleared all cache")
    }

    /**
     * 캐시 상태 로그 출력
     */
    fun logCacheStatus() {
        Timber.d("Cache status: ${cacheMap.size} restaurants cached")
        cacheMap.forEach { (restaurant, data) ->
            Timber.d("${restaurant.name}: ${data.date} at ${data.timestamp}")
        }
    }
}