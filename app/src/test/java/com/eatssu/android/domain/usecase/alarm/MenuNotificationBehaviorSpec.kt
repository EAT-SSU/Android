package com.eatssu.android.domain.usecase.alarm

import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
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
