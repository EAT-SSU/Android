package com.eatssu.android.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eatssu.android.domain.model.notification.MenuNotification
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

private val Context.notificationDataStore by preferencesDataStore(name = "notification_inbox")

@Singleton
class NotificationStore @Inject constructor(@ApplicationContext private val context: Context) {
    private val key = stringPreferencesKey("items")
    private val json = Json { ignoreUnknownKeys = true }
    private fun decode(value: String?): List<MenuNotification> =
        value?.let { runCatching { json.decodeFromString<List<MenuNotification>>(it) }.getOrNull() }
            ?: emptyList()

    // Refresh while observing, so expiry and relative timestamps update on an open screen.
    val notifications = combine(
        context.notificationDataStore.data.map { decode(it[key]) },
        flow {
            while (true) {
                emit(Instant.now()); delay(60_000)
            }
        },
    ) { items, now -> items.filterNot { it.isExpired(now) }.sortedByDescending { it.receivedAt } }

    suspend fun add(notification: MenuNotification) = update { items ->
        if (items.any { it.id == notification.id }) items else items + notification
    }

    suspend fun markRead(id: String) = update { items ->
        items.map { if (it.id == id) it.copy(isRead = true) else it }
    }

    suspend fun markAllRead() = update { items -> items.map { it.copy(isRead = true) } }
    suspend fun prune() = update { it }
    suspend fun clear() {
        context.notificationDataStore.edit { it.clear() }
    }

    private suspend fun update(transform: (List<MenuNotification>) -> List<MenuNotification>) {
        context.notificationDataStore.edit { preferences ->
            val now = Instant.now()
            preferences[key] = json.encodeToString(
                transform(decode(preferences[key])).filterNot { it.isExpired(now) },
            )
        }
    }
}
