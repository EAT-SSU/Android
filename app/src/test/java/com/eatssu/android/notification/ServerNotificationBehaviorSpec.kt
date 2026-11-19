package com.eatssu.android.notification

import com.eatssu.android.data.local.NotificationStore
import com.eatssu.android.data.local.TokenStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.repository.NotificationRepository
import com.eatssu.android.data.remote.repository.toNotification
import com.eatssu.android.data.remote.service.DeletePushTokenRequest
import com.eatssu.android.data.remote.service.NotificationService
import com.eatssu.android.data.remote.service.PushTokenRequest
import com.eatssu.android.data.remote.service.ServerNotificationResponse
import com.eatssu.android.data.remote.service.UnreadNotificationResponse
import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.domain.model.notification.NotificationSource
import com.eatssu.android.test.AppBehaviorSpec
import com.eatssu.common.enums.Time
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

private fun response() = ServerNotificationResponse(
    notificationId = 42, type = "MENU_FAVORITE", title = "Favorite menu", body = "Today's menu",
    date = "20261119", timePart = "LUNCH", isRead = false, createdAt = "2026-11-19T11:00:00",
)

class ServerNotificationBehaviorSpec : AppBehaviorSpec({
    given("server notification payloads") {
        then("maps date, time, read flag and Seoul creation time") {
            val item = requireNotNull(response().toNotification())
            item.id shouldBe "server_42"
            item.serverNotificationId shouldBe 42L
            item.mealDate shouldBe "2026-11-19"
            item.mealTime shouldBe Time.LUNCH
            item.receivedAt shouldBe Instant.parse("2026-11-19T02:00:00Z").toEpochMilli()
            item.isRead shouldBe false
        }
        then("ignores unsupported types and invalid destinations") {
            response().copy(type = "FUTURE_TYPE").toNotification() shouldBe null
            response().copy(timePart = "UNKNOWN").toNotification() shouldBe null
            response().copy(date = "invalid").toNotification() shouldBe null
            response().copy(createdAt = "invalid").toNotification() shouldBe null
        }
        then("uses one stable id for API and foreground FCM payloads") {
            val data = mapOf(
                "type" to "MENU_FAVORITE", "date" to "20261119", "timePart" to "LUNCH",
                "notificationId" to "42", "title" to "Favorite menu", "body" to "Today's menu",
            )
            val message = requireNotNull(MenuNotification.fromRemoteData(data, "fcm-id", 1L, "Notice"))
            message.id shouldBe response().toNotification()?.id
            message.mealDate shouldBe "2026-11-19"
            message.mealTime shouldBe Time.LUNCH
            MenuNotification.fromRemoteData(data - "notificationId", "fcm-id", 1L, "Notice") shouldBe null
        }
        then("parses a background tap without notification text") {
            MenuNotification.fromData(mapOf(
                "type" to "MENU_FAVORITE", "date" to "20261119", "timePart" to "DINNER",
                "notificationId" to "42",
            ), "fcm-id", 1L)?.mealTime shouldBe Time.DINNER
        }
        then("keeps legacy console notification compatibility") {
            MenuNotification.fromRemoteData(mapOf("title" to "Notice", "body" to "Text"), "legacy", 1L, "Notice")
                ?.id shouldBe "legacy"
        }
        then("sends the required device type even with encodeDefaults disabled") {
            Json.encodeToString(PushTokenRequest("example", "ANDROID")) shouldBe
                "{\"token\":\"example\",\"deviceType\":\"ANDROID\"}"
            Json.encodeToString(DeletePushTokenRequest("example")) shouldBe "{\"token\":\"example\"}"
        }
    }

    given("server inbox repository") {
        fun setup(): Triple<NotificationRepository, NotificationService, MutableStateFlow<List<MenuNotification>>> {
            val service = mockk<NotificationService>()
            val local = mockk<NotificationStore>(relaxed = true)
            val tokens = mockk<TokenStore>()
            val cached = MutableStateFlow<List<MenuNotification>>(emptyList())
            every { local.notifications } returns cached
            every { tokens.accessToken } returns "account"
            coEvery { service.getNotifications() } returns ApiResult.Success(listOf(response()))
            coEvery { service.getUnread() } returns ApiResult.Success(UnreadNotificationResponse(false))
            coEvery { service.markRead(any()) } returns ApiResult.Success(Unit)
            coEvery { service.markAllRead() } returns ApiResult.Success(Unit)
            return Triple(NotificationRepository(service, local, tokens), service, cached)
        }
        then("merges general local reminders and hides old locally generated favorite alerts") {
            val (repository, _, cached) = setup()
            runBlocking {
                val now = System.currentTimeMillis()
                cached.value = listOf(
                    MenuNotification("local_2026-11-19_daily", "Daily", "Body", now, source = NotificationSource.LOCAL),
                    MenuNotification("local_2026-11-19_lunch", "Old favorite", "Body", now, source = NotificationSource.LOCAL),
                )
                repository.refresh()
                val items = withTimeout(5_000) { repository.notifications.first { it.size == 2 } }
                items.map { it.id }.toSet() shouldBe setOf("local_2026-11-19_daily", "server_42")
            }
        }
        then("marks a server notification read through the API") {
            val (repository, service, _) = setup()
            runBlocking {
                repository.refresh()
                repository.markRead("server_42") shouldBe true
                withTimeout(5_000) { repository.notifications.first { it.singleOrNull()?.isRead == true } }
                coVerify { service.markRead(42L) }
            }
        }
        then("does not claim read success when the server rejects it") {
            val (repository, service, _) = setup()
            runBlocking {
                repository.refresh()
                coEvery { service.markRead(any()) } returns ApiResult.Failure(500, "failed")
                repository.markRead("server_42") shouldBe false
                withTimeout(5_000) { repository.notifications.first { it.size == 1 } }.single().isRead shouldBe false
            }
        }
        then("preserves previous data and exposes failures instead of an empty inbox") {
            val (repository, service, _) = setup()
            runBlocking {
                repository.refresh()
                coEvery { service.getNotifications() } returns ApiResult.Failure(500, "failed")
                repository.refresh()
                repository.error.value shouldBe true
                withTimeout(5_000) { repository.notifications.first { it.size == 1 } }.single().id shouldBe "server_42"
            }
        }
        then("marks all notifications through the server and clears the session") {
            val (repository, service, _) = setup()
            runBlocking {
                repository.refresh()
                repository.markAllRead() shouldBe true
                coVerify { service.markAllRead() }
                withTimeout(5_000) { repository.notifications.first { it.singleOrNull()?.isRead == true } }
                repository.clearSession()
                withTimeout(5_000) { repository.notifications.first { it.isEmpty() } }
            }
        }
    }
})
