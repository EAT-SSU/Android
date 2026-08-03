package com.eatssu.android.presentation.map

import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.android.test.samplePartnershipRestaurant
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class MapWebFallbackBehaviorSpec : AppBehaviorSpec({

    given("a partnership without server map URLs") {
        val restaurant = samplePartnershipRestaurant().copy(
            storeName = "놀숲 숭실대점",
            latitude = 37.495,
            longitude = 126.957,
            naverMapUrl = null,
            kakaoMapUrl = null,
        )

        `when`("the Naver Map app is unavailable") {
            then("a browser search URL is available") {
                MapDeepLink.fallbackWebUrl(
                    provider = MapProvider.NAVER,
                    restaurant = restaurant,
                    resolvedPlace = null,
                ) shouldContain "https://map.naver.com/p/search/"
            }
        }

        `when`("the KakaoMap app is unavailable") {
            then("a browser coordinate URL is available") {
                MapDeepLink.fallbackWebUrl(
                    provider = MapProvider.KAKAO,
                    restaurant = restaurant,
                    resolvedPlace = null,
                ) shouldBe "https://map.kakao.com/link/map/" +
                    "%EB%86%80%EC%88%B2%20%EC%88%AD%EC%8B%A4%EB%8C%80%EC%A0%90,37.495,126.957"
            }
        }
    }

    given("a malformed server URL") {
        `when`("the URL is selected for navigation") {
            then("it is rejected so a generated fallback can be used") {
                MapDeepLink.serverWebUrl(
                    provider = MapProvider.NAVER,
                    restaurant = samplePartnershipRestaurant(
                        naverMapUrl = "not-a-web-url",
                    ),
                ) shouldBe null
            }
        }
    }
})
