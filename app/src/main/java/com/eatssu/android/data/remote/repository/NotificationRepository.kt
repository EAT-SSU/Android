package com.eatssu.android.data.remote.repository

import com.eatssu.android.data.local.NotificationStore
import com.eatssu.android.data.local.TokenStore
import com.eatssu.android.data.model.ApiResult
import com.eatssu.android.data.remote.service.NotificationService
import com.eatssu.android.data.remote.service.ServerNotificationResponse
import com.eatssu.android.domain.model.notification.MenuNotification
import com.eatssu.android.domain.model.notification.NotificationSource
import com.eatssu.common.enums.Time
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val service: NotificationService,
    private val local: NotificationStore,
    private val tokens: TokenStore,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutex = Mutex()
    private val remote = MutableStateFlow<List<MenuNotification>>(emptyList())
    private val remoteUnread = MutableStateFlow(false)
    val loading = MutableStateFlow(false)
    val error = MutableStateFlow(false)
    val notifications = combine(local.notifications, remote) { cached, server ->
        // Older releases generated favorite notifications locally. The server now owns those.
        (cached.filter {
            it.source == NotificationSource.LOCAL &&
                !Regex("local_\\d{4}-\\d{2}-\\d{2}_(breakfast|lunch|dinner)").matches(it.id)
        } + server)
            .sortedByDescending { it.receivedAt }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())
    val hasUnread = combine(notifications, remoteUnread) { items, unread ->
        unread || items.any { !it.isRead }
    }.stateIn(scope, SharingStarted.Eagerly, false)

    suspend fun refresh() = mutex.withLock {
        val account = tokens.accessToken
        if (account.isBlank()) {
            clearSession()
            return@withLock
        }
        loading.value = true
        try {
            val result = service.getNotifications()
            if (account != tokens.accessToken) return@withLock
            when (result) {
                is ApiResult.Success -> {
                    remote.value = result.data.mapNotNull { it.toNotification() }
                    remoteUnread.value = result.data.any { !it.isRead }
                    error.value = false
                }
                else -> error.value = true
            }
        } finally {
            loading.value = false
        }
    }

    suspend fun refreshUnread() {
        val account = tokens.accessToken
        if (account.isBlank()) return
        val result = service.getUnread()
        if (account == tokens.accessToken && result is ApiResult.Success) {
            remoteUnread.value = result.data.hasUnread
        }
    }

    fun onPushReceived(message: MenuNotification) {
        if (tokens.accessToken.isBlank() || message.serverNotificationId == null) return
        val previous = remote.value.firstOrNull { it.id == message.id }
        val incoming = message.copy(isRead = previous?.isRead ?: message.isRead)
        remote.value = (remote.value.filterNot { it.id == message.id } + incoming)
        if (!incoming.isRead) remoteUnread.value = true
    }

    suspend fun markRead(id: String): Boolean {
        val serverId = id.removePrefix("server_").takeIf { id.startsWith("server_") }?.toLongOrNull()
        if (serverId == null) {
            local.markRead(id)
            return true
        }
        val account = tokens.accessToken.takeIf { it.isNotBlank() } ?: return false
        if (service.markRead(serverId) !is ApiResult.Success || account != tokens.accessToken) return false
        remote.value = remote.value.map { if (it.id == id) it.copy(isRead = true) else it }
        refreshUnread()
        return true
    }

    suspend fun markAllRead(): Boolean {
        val account = tokens.accessToken
        if (account.isNotBlank()) {
            if (service.markAllRead() !is ApiResult.Success || account != tokens.accessToken) return false
        }
        local.markAllRead()
        remote.value = remote.value.map { it.copy(isRead = true) }
        remoteUnread.value = false
        return true
    }

    fun clearSession() {
        remote.value = emptyList()
        remoteUnread.value = false
        error.value = false
    }
}

fun ServerNotificationResponse.toNotification(): MenuNotification? {
    if (type != "MENU_FAVORITE" || notificationId <= 0) return null
    val mealDate = runCatching { LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE) }.getOrNull() ?: return null
    val time = Time.entries.firstOrNull { it.name == timePart } ?: return null
    val receivedAt = runCatching {
        LocalDateTime.parse(createdAt).atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli()
    }.getOrNull() ?: return null
    return MenuNotification(
        id = "server_$notificationId", title = title, body = body, receivedAt = receivedAt,
        mealDate = mealDate.toString(), mealTime = time, isRead = isRead,
        serverNotificationId = notificationId,
    )
}
