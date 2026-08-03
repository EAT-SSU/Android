package com.eatssu.android.presentation.map

import com.eatssu.android.test.AppBehaviorSpec
import io.kotest.matchers.shouldBe
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MapDeepLinkBehaviorSpec : AppBehaviorSpec({

    given("네이버 지도 앱 딥링크") {
        `when`("상호명과 앱 ID를 전달하면") {
            val url = MapDeepLink.naverSearchUrl(
                storeName = "현선이네 숭실대점",
                appName = "com.eatssu.android.debug",
            )

            then("상호명이 인코딩된 검색 URL을 만든다") {
                url.substringBefore('?') shouldBe "nmap://search"
                url.queryParameter("query") shouldBe "현선이네 숭실대점"
                url.queryParameter("appname") shouldBe "com.eatssu.android.debug"
            }
        }
    }

    given("카카오맵 앱 딥링크") {
        `when`("서버 URL에 장소 ID가 있으면") {
            then("해당 장소 상세 URL을 만든다") {
                MapDeepLink.kakaoPlaceUrl("273098143") shouldBe
                    "kakaomap://place?id=273098143"
            }
        }

        `when`("정확한 상호명을 조회했으면") {
            then("상호명과 좌표를 포함한 검색 URL을 만든다") {
                MapDeepLink.kakaoSearchUrl(
                    storeName = "크라이치즈버거 숭실대점",
                    latitude = 37.4987842,
                    longitude = 126.9520631,
                ).queryParameter("q") shouldBe "크라이치즈버거 숭실대점"
            }
        }

        `when`("장소 ID가 숫자가 아니면") {
            then("장소 ID로 사용하지 않는다") {
                MapDeepLink.kakaoPlaceId(
                    "https://place.map.kakao.com/not-a-place-id"
                ) shouldBe null
            }
        }

        `when`("카카오맵 버전이 6 이상이면") {
            then("장소 ID 액션을 지원한다") {
                MapDeepLink.supportsKakaoPlaceAction("6.0.0") shouldBe true
                MapDeepLink.supportsKakaoPlaceAction("7.1.2") shouldBe true
            }
        }

        `when`("카카오맵 버전이 6 미만이면") {
            then("장소 ID 액션 대신 검색 폴백을 사용한다") {
                MapDeepLink.supportsKakaoPlaceAction("1.12.1") shouldBe false
                MapDeepLink.supportsKakaoPlaceAction(null) shouldBe false
            }
        }
    }
})

private fun String.queryParameter(name: String): String? =
    substringAfter('?', missingDelimiterValue = "")
        .split('&')
        .mapNotNull { pair ->
            val separatorIndex = pair.indexOf('=')
            if (separatorIndex < 0) return@mapNotNull null

            val key = pair.substring(0, separatorIndex)
            val value = pair.substring(separatorIndex + 1)
            key to URLDecoder.decode(value, StandardCharsets.UTF_8.name())
        }
        .firstOrNull { (key, _) -> key == name }
        ?.second
