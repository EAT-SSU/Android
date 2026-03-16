package com.eatssu.common.analytics

import com.eatssu.common.enums.LaunchPath
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.ScreenId
import com.eatssu.common.enums.Time

sealed interface AnalyticsEvent {
    val eventName: String
    val properties: Map<String, Any>

    fun toPayload(): AnalyticsPayload = AnalyticsPayload(
        eventName = eventName,
        properties = properties,
    )
}

sealed interface AppAnalyticsEvent : AnalyticsEvent {
    data class Launch(
        val launchPath: LaunchPath,
    ) : AppAnalyticsEvent {
        override val eventName = "app_launch"
        override val properties = buildMap {
            put("launch_path", launchPath.value)
        }
    }
}

sealed interface CafeteriaAnalyticsEvent : AnalyticsEvent {
    data class RestaurantInfoClicked(
        val restaurant: Restaurant,
    ) : CafeteriaAnalyticsEvent {
        override val eventName = "click_restaurant_info"
        override val properties = buildMap {
            put("restaurants", restaurant.value)
        }
    }

    data class MealTimeSelected(
        val time: Time,
    ) : CafeteriaAnalyticsEvent {
        override val eventName = "select_mealtime"
        override val properties = buildMap {
            put("mealtime", time.value)
        }
    }

    data class DaySelected(
        val day: String,
    ) : CafeteriaAnalyticsEvent {
        override val eventName = "click_day"
        override val properties = buildMap {
            put("day", day)
        }
    }

    data class MenuClicked(
        val restaurant: Restaurant,
    ) : CafeteriaAnalyticsEvent {
        override val eventName = "click_menu"
        override val properties = buildMap {
            put("restaurants", restaurant.value)
        }
    }
}

sealed interface ReviewAnalyticsEvent : AnalyticsEvent {
    object WriteClicked : ReviewAnalyticsEvent {
        override val eventName = "write_review_v2"
        override val properties = emptyMap<String, Any>()
    }

    data class Completed(
        val rating: Long,
        val likes: Long,
        val photoAttached: Boolean,
    ) : ReviewAnalyticsEvent {
        override val eventName = "complete_review_v2"
        override val properties = buildMap {
            put("rating", rating)
            put("likes", likes)
            put("photo_attached", photoAttached)
        }
    }
}

sealed interface MapAnalyticsEvent : AnalyticsEvent {
    object AllClicked : MapAnalyticsEvent {
        override val eventName = "click_map"
        override val properties = emptyMap<String, Any>()
    }

    data class MineClicked(
        val college: Long,
        val major: Long,
    ) : MapAnalyticsEvent {
        override val eventName = "click_map_mine"
        override val properties = buildMap {
            put("college", college)
            put("major", major)
        }
    }

    data class PartnerRestaurantClicked(
        val college: Long,
        val major: Long,
        val partnerRestaurantId: Long,
    ) : MapAnalyticsEvent {
        override val eventName = "click_partner_restaurant"
        override val properties = buildMap {
            put("college", college)
            put("major", major)
            put("partner_restaurant_id", partnerRestaurantId)
        }
    }
}

sealed interface WidgetAnalyticsEvent : AnalyticsEvent {
    data class Added(
        val restaurant: Restaurant,
    ) : WidgetAnalyticsEvent {
        override val eventName = "add_widget"
        override val properties = buildMap {
            put("restaurants", restaurant.value)
        }
    }

    data class Removed(
        val restaurant: Restaurant? = null,
    ) : WidgetAnalyticsEvent {
        override val eventName = "remove_widget"
        override val properties = buildMap {
            restaurant?.let { put("restaurants", it.value) }
        }
    }

    data class Changed(
        val restaurant: Restaurant,
    ) : WidgetAnalyticsEvent {
        override val eventName = "change_widget"
        override val properties = buildMap {
            put("restaurants", restaurant.value)
        }
    }
}

data class ScreenViewEvent(
    val screenId: ScreenId,
    val screenClass: String? = null,
) : AnalyticsEvent {
    override val eventName = "screen_view"
    override val properties = buildMap {
        put("screen_name", screenId.value)
        screenClass?.let { put("screen_class", it) }
    }
}

private fun String.toWeekdayCode() = when (this) {
    "SUNDAY" -> "sun"
    "MONDAY" -> "mon"
    "TUESDAY" -> "tue"
    "WEDNESDAY" -> "wed"
    "THURSDAY" -> "thu"
    "FRIDAY" -> "fri"
    "SATURDAY" -> "sat"
    else -> ""
}
