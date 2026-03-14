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
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(LAUNCH_PATH_KEY to launchPath.value)

        companion object {
            const val EVENT_NAME = "app_launch"
            const val LAUNCH_PATH_KEY = "launch_path"
        }
    }
}

sealed interface CafeteriaAnalyticsEvent : AnalyticsEvent {
    data class RestaurantInfoClicked(
        val restaurant: Restaurant,
    ) : CafeteriaAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(RESTAURANTS_KEY to restaurant.value)

        companion object {
            const val EVENT_NAME = "click_restaurant_info"
            const val RESTAURANTS_KEY = "restaurants"
        }
    }

    data class MealTimeSelected(
        val time: Time,
    ) : CafeteriaAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(MEAL_TIME_KEY to time.value)

        companion object {
            const val EVENT_NAME = "select_mealtime"
            const val MEAL_TIME_KEY = "mealtime"
        }
    }

    data class DaySelected(
        val day: String,
    ) : CafeteriaAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(DAY_KEY to day.toWeekdayCode())

        companion object {
            const val EVENT_NAME = "click_day"
            const val DAY_KEY = "day"
        }
    }

    data class MenuClicked(
        val restaurant: Restaurant,
    ) : CafeteriaAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(RESTAURANTS_KEY to restaurant.value)

        companion object {
            const val EVENT_NAME = "click_menu"
            const val RESTAURANTS_KEY = "restaurants"
        }
    }
}

sealed interface ReviewAnalyticsEvent : AnalyticsEvent {
    object WriteClicked : ReviewAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> = emptyMap()

        const val EVENT_NAME = "write_review_v2"
    }

    data class Completed(
        val rating: Long,
        val likes: Long,
        val photoAttached: Boolean,
    ) : ReviewAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(
                RATING_KEY to rating,
                LIKES_KEY to likes,
                PHOTO_ATTACHED_KEY to if (photoAttached) 1 else 0,
            )

        companion object {
            const val EVENT_NAME = "complete_review_v2"
            const val RATING_KEY = "rating"
            const val LIKES_KEY = "likes"
            const val PHOTO_ATTACHED_KEY = "photo_attached"
        }
    }
}

sealed interface MapAnalyticsEvent : AnalyticsEvent {
    object AllClicked : MapAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> = emptyMap()

        const val EVENT_NAME = "click_map"
    }

    data class MineClicked(
        val college: Long,
        val major: Long,
    ) : MapAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(
                COLLEGE_KEY to college,
                MAJOR_KEY to major,
            )

        companion object {
            const val EVENT_NAME = "click_map_mine"
            const val COLLEGE_KEY = "college"
            const val MAJOR_KEY = "major"
        }
    }

    data class PartnerRestaurantClicked(
        val college: Long,
        val major: Long,
        val partnerRestaurantId: Long,
    ) : MapAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(
                COLLEGE_KEY to college,
                MAJOR_KEY to major,
                PARTNER_RESTAURANT_ID_KEY to partnerRestaurantId,
            )

        companion object {
            const val EVENT_NAME = "click_partner_restaurant"
            const val COLLEGE_KEY = "college"
            const val MAJOR_KEY = "major"
            const val PARTNER_RESTAURANT_ID_KEY = "partner_restaurant_id"
        }
    }
}

sealed interface WidgetAnalyticsEvent : AnalyticsEvent {
    data class Added(
        val restaurant: Restaurant,
    ) : WidgetAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(RESTAURANTS_KEY to restaurant.value)

        companion object {
            const val EVENT_NAME = "add_widget"
            const val RESTAURANTS_KEY = "restaurants"
        }
    }

    data class Removed(
        val restaurant: Restaurant? = null,
    ) : WidgetAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            buildMap {
                restaurant?.let { put(RESTAURANTS_KEY, it.value) }
            }

        companion object {
            const val EVENT_NAME = "remove_widget"
            const val RESTAURANTS_KEY = "restaurants"
        }
    }

    data class Changed(
        val restaurant: Restaurant,
    ) : WidgetAnalyticsEvent {
        override val eventName: String = EVENT_NAME
        override val properties: Map<String, Any> =
            mapOf(RESTAURANTS_KEY to restaurant.value)

        companion object {
            const val EVENT_NAME = "change_widget"
            const val RESTAURANTS_KEY = "restaurants"
        }
    }
}

data class ScreenViewEvent(
    val screenId: ScreenId,
    val screenClass: String? = null,
) : AnalyticsEvent {
    override val eventName: String = EVENT_NAME
    override val properties: Map<String, Any> =
        buildMap {
            put(SCREEN_NAME_KEY, screenId.value)
            screenClass?.let { put(SCREEN_CLASS_KEY, it) }
        }

    companion object {
        const val EVENT_NAME = "screen_view"
        const val SCREEN_NAME_KEY = "screen_name"
        const val SCREEN_CLASS_KEY = "screen_class"
    }
}

private fun String.toWeekdayCode(): String =
    when (this) {
        "SUNDAY" -> "sun"
        "MONDAY" -> "mon"
        "TUESDAY" -> "tue"
        "WEDNESDAY" -> "wed"
        "THURSDAY" -> "thu"
        "FRIDAY" -> "fri"
        "SATURDAY" -> "sat"
        else -> ""
    }
