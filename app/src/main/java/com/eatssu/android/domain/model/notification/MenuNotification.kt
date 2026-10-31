package com.eatssu.android.domain.model.notification

import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

@Serializable
enum class NotificationSource { LOCAL, REMOTE }

@Serializable
data class MenuNotification(
    val id: String,
    val title: String,
    val body: String,
    val receivedAt: Long,
    val mealDate: String? = null,
    val mealTime: Time? = null,
    val isRead: Boolean = false,
    val targetRestaurant: Restaurant? = null,
    val source: NotificationSource = NotificationSource.REMOTE,
) {
    fun isExpired(now: Instant): Boolean =
        !Instant.ofEpochMilli(receivedAt).plus(Duration.ofDays(7)).isAfter(now)

    companion object {
        fun fromRemoteData(
            data: Map<String, String>,
            id: String,
            sentAt: Long,
            fallbackTitle: String
        ): MenuNotification? {
            val body = data["body"].orEmpty()
            val title = data["title"].orEmpty()
            if (title.isBlank() && body.isBlank()) return null
            val message = data + ("title" to title.ifBlank { fallbackTitle })
            return fromData(message, id, sentAt) ?: MenuNotification(
                id = id, title = message.getValue("title"), body = body, receivedAt = sentAt,
            )
        }

        // Explicit destination data is required: never infer an old notification's date from today.
        fun fromData(data: Map<String, String>, id: String, sentAt: Long): MenuNotification? {
            val date = runCatching { LocalDate.parse(data["meal_date"]) }.getOrNull() ?: return null
            val time = Time.entries.firstOrNull {
                it.value.equals(data["meal_time"], ignoreCase = true) ||
                        it.name.equals(data["meal_time"], ignoreCase = true)
            } ?: return null
            val title = data["title"]?.takeIf { it.isNotBlank() } ?: return null
            val body = data["body"]?.takeIf { it.isNotBlank() } ?: return null
            val restaurants = listOfNotNull(data["restaurant"], data["restaurants"])
                .flatMap { it.split(',') }
                .mapNotNull { value ->
                    Restaurant.entries.firstOrNull {
                        it.name.equals(value.trim(), true) || it.value.equals(value.trim(), true)
                    }
                }
            return MenuNotification(
                id, title, body, sentAt, date.toString(), time,
                targetRestaurant = restaurants.minByOrNull { it.ordinal },
            )
        }
    }
}
