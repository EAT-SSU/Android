package com.eatssu.android.presentation.widget

import com.eatssu.android.domain.model.WidgetMealInfo
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import io.kotest.matchers.shouldBe
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

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
                val baseClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC)
                WidgetCacheManager.clearAllCache()
                WidgetCacheManager.cacheMealData(restaurant, mealInfo, "20250101", baseClock)

                WidgetCacheManager.getCachedMealData(restaurant, "20250101", baseClock) shouldBe mealInfo
            }
        }

        `when`("다른 날짜로 조회하면") {
            then("캐시를 무효화하고 null을 반환한다") {
                val baseClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC)
                WidgetCacheManager.clearAllCache()
                WidgetCacheManager.cacheMealData(restaurant, mealInfo, "20250101", baseClock)

                WidgetCacheManager.getCachedMealData(restaurant, "20250102", baseClock) shouldBe null
            }
        }

        `when`("캐시가 30분 경계를 넘기면") {
            then("null을 반환한다") {
                val cachedAt = Clock.fixed(Instant.parse("2025-01-01T10:00:00Z"), ZoneOffset.UTC)
                val queriedAt = Clock.fixed(Instant.parse("2025-01-01T10:30:00Z"), ZoneOffset.UTC)
                WidgetCacheManager.clearAllCache()
                WidgetCacheManager.cacheMealData(restaurant, mealInfo, "20250101", cachedAt)

                WidgetCacheManager.getCachedMealData(restaurant, "20250101", queriedAt) shouldBe null
            }
        }

        `when`("식당별 캐시를 삭제하면") {
            then("해당 식당 캐시는 제거된다") {
                val baseClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC)
                WidgetCacheManager.clearAllCache()
                WidgetCacheManager.cacheMealData(restaurant, mealInfo, "20250101", baseClock)
                WidgetCacheManager.clearCacheForRestaurant(restaurant)

                WidgetCacheManager.getCachedMealData(restaurant, "20250101", baseClock) shouldBe null
            }
        }

        `when`("전체 캐시를 삭제하면") {
            then("모든 식당 캐시가 제거된다") {
                val baseClock = Clock.fixed(Instant.parse("2025-01-01T00:00:00Z"), ZoneOffset.UTC)
                WidgetCacheManager.clearAllCache()
                WidgetCacheManager.cacheMealData(Restaurant.HAKSIK, mealInfo, "20250101", baseClock)
                WidgetCacheManager.cacheMealData(
                    Restaurant.DODAM,
                    mealInfo.copy(restaurant = Restaurant.DODAM),
                    "20250101",
                    baseClock,
                )
                WidgetCacheManager.clearAllCache()

                WidgetCacheManager.getCachedMealData(Restaurant.HAKSIK, "20250101", baseClock) shouldBe null
                WidgetCacheManager.getCachedMealData(Restaurant.DODAM, "20250101", baseClock) shouldBe null
            }
        }
    }
})
