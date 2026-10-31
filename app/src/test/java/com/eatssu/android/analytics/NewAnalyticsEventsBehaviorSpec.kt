package com.eatssu.android.analytics

import com.eatssu.common.analytics.GoodPriceAnalyticsEvent
import com.eatssu.common.analytics.MapAnalyticsEvent
import com.eatssu.common.analytics.ReviewAnalyticsEvent
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class NewAnalyticsEventsBehaviorSpec : BehaviorSpec({

    Given("click_hankki 이벤트가 발생했을 때") {
        When("이벤트를 생성하면") {
            val event = GoodPriceAnalyticsEvent.HankkiClicked

            Then("파라미터 없이 올바른 eventName을 가져야 한다") {
                event.eventName shouldBe "click_hankki"
                event.properties shouldBe emptyMap()
            }
        }
    }

    Given("translate_review 이벤트가 발생했을 때") {
        When("lang_to 파라미터와 함께 이벤트를 생성하면") {
            val event = ReviewAnalyticsEvent.TranslateClicked(langTo = "en")

            Then("올바른 eventName과 lang_to 프로퍼티를 가져야 한다") {
                event.eventName shouldBe "translate_review"
                event.properties shouldBe mapOf("lang_to" to "en")
            }
        }
    }

    Given("click_map_jehu 이벤트가 발생했을 때") {
        When("college, major, default_type과 함께 이벤트를 생성하면") {
            val event = MapAnalyticsEvent.JehuClicked(
                college = 1L,
                major = 10L,
                defaultType = "general",
            )

            Then("올바른 eventName과 프로퍼티를 가져야 한다") {
                event.eventName shouldBe "click_map_jehu"
                event.properties shouldBe mapOf(
                    "college" to 1L,
                    "major" to 10L,
                    "default_type" to "general",
                )
            }
        }
    }

    Given("click_map_hankki 이벤트가 발생했을 때") {
        When("college, major와 함께 이벤트를 생성하면") {
            val event = MapAnalyticsEvent.HankkiClicked(
                college = 1L,
                major = 10L,
            )

            Then("올바른 eventName과 프로퍼티를 가져야 한다") {
                event.eventName shouldBe "click_map_hankki"
                event.properties shouldBe mapOf(
                    "college" to 1L,
                    "major" to 10L,
                )
            }
        }
    }

    Given("click_map_restaurant_deeplink 이벤트가 발생했을 때") {
        When("navermap 딥링크로 이벤트를 생성하면") {
            val event = MapAnalyticsEvent.RestaurantDeeplinkClicked(
                college = 1L,
                major = 10L,
                deeplink = "navermap",
            )

            Then("올바른 eventName과 프로퍼티를 가져야 한다") {
                event.eventName shouldBe "click_map_restaurant_deeplink"
                event.properties shouldBe mapOf(
                    "college" to 1L,
                    "major" to 10L,
                    "deeplink" to "navermap",
                )
            }
        }

        When("kakaomap 딥링크로 이벤트를 생성하면") {
            val event = MapAnalyticsEvent.RestaurantDeeplinkClicked(
                college = 1L,
                major = 10L,
                deeplink = "kakaomap",
            )

            Then("올바른 eventName과 프로퍼티를 가져야 한다") {
                event.eventName shouldBe "click_map_restaurant_deeplink"
                event.properties shouldBe mapOf(
                    "college" to 1L,
                    "major" to 10L,
                    "deeplink" to "kakaomap",
                )
            }
        }
    }
})
