package com.eatssu.android.presentation.widget

import com.eatssu.android.domain.model.WidgetMealInfo
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class WidgetCacheManagerBehaviorSpec : AppBehaviorSpec({

    given("WidgetCacheManager") {
        val restaurant = Restaurant.HAKSIK
        val mealInfo = WidgetMealInfo.Available(
            breakfast = listOf(listOf("아침")),
            lunch = listOf(listOf("점심")),
            dinner = listOf(listOf("저녁")),
            restaurant = restaurant,
        )

        `when`("같은 날짜로 캐시 조회하면") {
            then("캐시된 데이터를 반환한다") {
                WidgetCacheManager.clearAllCache()
                WidgetCacheManager.cacheMealData(restaurant, mealInfo, "20250101")

                WidgetCacheManager.getCachedMealData(restaurant, "20250101") shouldBe mealInfo
            }
        }

        `when`("다른 날짜로 조회하면") {
            then("캐시를 무효화하고 null을 반환한다") {
                WidgetCacheManager.clearAllCache()
                WidgetCacheManager.cacheMealData(restaurant, mealInfo, "20250101")

                WidgetCacheManager.getCachedMealData(restaurant, "20250102") shouldBe null
            }
        }

        `when`("캐시가 30분 초과로 만료되면") {
            then("null을 반환한다") {
                WidgetCacheManager.clearAllCache()

                @Suppress("UNCHECKED_CAST")
                val cacheMap = WidgetCacheManager::class.java
                    .getDeclaredField("cacheMap")
                    .apply { isAccessible = true }
                    .get(WidgetCacheManager) as MutableMap<Restaurant, WidgetCacheManager.CachedMealData>

                cacheMap[restaurant] = WidgetCacheManager.CachedMealData(
                    mealInfo = mealInfo,
                    timestamp = LocalDateTime.now().minusMinutes(31),
                    date = "20250101",
                )

                WidgetCacheManager.getCachedMealData(restaurant, "20250101") shouldBe null
            }
        }

        `when`("식당별 캐시를 삭제하면") {
            then("해당 식당 캐시는 제거된다") {
                WidgetCacheManager.clearAllCache()
                WidgetCacheManager.cacheMealData(restaurant, mealInfo, "20250101")
                WidgetCacheManager.clearCacheForRestaurant(restaurant)

                WidgetCacheManager.getCachedMealData(restaurant, "20250101") shouldBe null
            }
        }

        `when`("전체 캐시를 삭제하면") {
            then("모든 식당 캐시가 제거된다") {
                WidgetCacheManager.clearAllCache()
                WidgetCacheManager.cacheMealData(Restaurant.HAKSIK, mealInfo, "20250101")
                WidgetCacheManager.cacheMealData(Restaurant.DODAM, mealInfo.copy(restaurant = Restaurant.DODAM), "20250101")
                WidgetCacheManager.clearAllCache()

                WidgetCacheManager.getCachedMealData(Restaurant.HAKSIK, "20250101") shouldBe null
                WidgetCacheManager.getCachedMealData(Restaurant.DODAM, "20250101") shouldBe null
            }
        }
    }
})
