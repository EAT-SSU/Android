package com.eatssu.android.domain.model.notification

import com.eatssu.common.enums.Time
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

@Serializable
data class MenuNotification(
    val id: String,
    val title: String,
    val body: String,
    val receivedAt: Long,
    val mealDate: String,
    val mealTime: Time,
    val isRead: Boolean = false,
) {
    fun isExpired(now: Instant): Boolean =
        !Instant.ofEpochMilli(receivedAt).plus(Duration.ofDays(7)).isAfter(now)

    companion object {
        // Explicit destination data is required: never infer an old notification's date from today.
        fun fromData(data: Map<String, String>, id: String, sentAt: Long): MenuNotification? {
            val date = runCatching { LocalDate.parse(data["meal_date"]) }.getOrNull() ?: return null
            val time = Time.entries.firstOrNull {
                it.value.equals(data["meal_time"], ignoreCase = true) ||
                        it.name.equals(data["meal_time"], ignoreCase = true)
            } ?: return null
            val title = data["title"]?.takeIf { it.isNotBlank() } ?: return null
            val body = data["body"]?.takeIf { it.isNotBlank() } ?: return null
            return MenuNotification(id, title, body, sentAt, date.toString(), time)
        }
    }
}
