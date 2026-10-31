package com.eatssu.android.domain.usecase.alarm

import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.domain.model.notification.NotificationSource
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

class MenuNotificationBehaviorSpec : AppBehaviorSpec({
    val sent = Instant.parse("2026-08-10T03:00:00Z")
    val data = mapOf(
        "title" to "🍽️ 찜한 메뉴가 나온대요!",
        "body" to "오늘 점심, 도담식당에 파스타 등장 👀",
        "meal_date" to "2026-08-10",
        "meal_time" to "lunch",
    )
    given("과거 식단 알림") {
        then("수신 시점과 무관하게 지정한 날짜와 식사 시간으로 연결한다") {
            val notification = MenuNotification.fromData(data, "id", sent.toEpochMilli())
            notification?.mealDate shouldBe "2026-08-10"
            notification?.mealTime shouldBe Time.LUNCH
            notification?.isRead shouldBe false
        }
        then("날짜나 식사 시간이 누락되거나 잘못되면 임의의 화면으로 연결하지 않는다") {
            MenuNotification.fromData(data - "meal_date", "id", 0) shouldBe null
            MenuNotification.fromData(data + ("meal_date" to "2026-02-30"), "id", 0) shouldBe null
            MenuNotification.fromData(data + ("meal_time" to "invalid"), "id", 0) shouldBe null
        }
        then("콘솔의 일반 제목·본문도 식단 날짜 없이 알림함에 저장할 수 있다") {
            val message = requireNotNull(
                MenuNotification.fromRemoteData(
                    mapOf(
                        "title" to "공지",
                        "body" to "서버 알림"
                    ), "remote", sent.toEpochMilli(), "알림"
                )
            )
            message.title shouldBe "공지"
            message.body shouldBe "서버 알림"
            message.mealDate shouldBe null
            message.targetRestaurant shouldBe null
            message.source shouldBe NotificationSource.REMOTE
            MenuNotification.fromRemoteData(emptyMap(), "empty", 0, "알림") shouldBe null
        }
        then("본문만 있는 FCM에도 기본 제목을 붙이고 잘못된 날짜를 추측하지 않는다") {
            val message = requireNotNull(
                MenuNotification.fromRemoteData(
                    mapOf(
                        "body" to "안내",
                        "meal_date" to "invalid"
                    ), "id", 0, "알림"
                )
            )
            message.title shouldBe "알림"
            message.mealDate shouldBe null
            message.mealTime shouldBe null
        }
        then("FCM이 여러 식당 정보를 제공하면 화면 순서상 첫 식당을 선택한다") {
            val message = MenuNotification.fromData(
                data + ("restaurants" to "DORMITORY,HAKSIK,DODAM"),
                "id",
                sent.toEpochMilli()
            )
            message?.targetRestaurant shouldBe Restaurant.HAKSIK
        }
        then("기존 알림의 읽음 데이터와 일반 알림의 null 목적지는 직렬화 후에도 유지한다") {
            val json = Json { ignoreUnknownKeys = true }
            val legacy =
                """{"id":"old","title":"알림","body":"본문","receivedAt":0,"mealDate":"2026-08-10","mealTime":"LUNCH","isRead":true}"""
            val old = json.decodeFromString<MenuNotification>(legacy)
            old.isRead shouldBe true
            old.source shouldBe NotificationSource.REMOTE
            val general =
                MenuNotification("generic", "일반", "본문", 0, source = NotificationSource.LOCAL)
            json.decodeFromString<MenuNotification>(json.encodeToString(general)) shouldBe general
        }
        then("7일이 되기 직전까지 보이고 정확히 7일 후에는 만료된다") {
            val notification =
                requireNotNull(MenuNotification.fromData(data, "id", sent.toEpochMilli()))
            notification.isExpired(sent.plusSeconds(7 * 86400 - 1)) shouldBe false
            notification.isExpired(sent.plusSeconds(7 * 86400)) shouldBe true
        }
        then("읽음으로 변경해도 이동 대상은 보존한다") {
            val notification =
                requireNotNull(MenuNotification.fromData(data, "id", sent.toEpochMilli()))
            notification.copy(isRead = true).mealDate shouldBe "2026-08-10"
            notification.copy(isRead = true).mealTime shouldBe Time.LUNCH
        }
    }
})
