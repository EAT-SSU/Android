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
        val localeCode: String,
    ) : AppAnalyticsEvent {
        override val eventName = "app_launch"
        override val properties = buildMap {
            put("launch_path", launchPath.value)
            put("localeCode", localeCode)
        }
    }
}

sealed interface CafeteriaAnalyticsEvent : AnalyticsEvent {
    data class RestaurantInfoClicked(
        val restaurant: Restaurant,
    ) : CafeteriaAnalyticsEvent {
        override val eventName = "click_restaurant_info"
        override val properties = buildMap {
            putRestaurant("restaurants", restaurant)
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
        override val eventName = "select_day"
        override val properties = buildMap {
            put("day", day.toWeekdayCode())
        }
    }

    data class MenuClicked(
        val restaurant: Restaurant,
    ) : CafeteriaAnalyticsEvent {
        override val eventName = "click_menu"
        override val properties = buildMap {
            putRestaurant("restaurants", restaurant)
        }
    }
}

sealed interface ReviewAnalyticsEvent : AnalyticsEvent {
    data class WriteClicked(
        val restaurant: Restaurant,
    ) : ReviewAnalyticsEvent {
        override val eventName = "write_review_v2"
        override val properties = buildMap {
            putRestaurant("restaurants", restaurant)
        }
    }

    data class Completed(
        val rating: Long,
        val likes: Long,
        val photoAttached: Boolean,
        val restaurant: Restaurant,
    ) : ReviewAnalyticsEvent {
        override val eventName = "complete_review_v2"
        override val properties = buildMap {
            put("rating", rating)
            put("likes", likes)
            put("photo_attached", if (photoAttached) 1 else 0)
            putRestaurant("restaurants", restaurant)
        }
    }
}

sealed interface MapAnalyticsEvent : AnalyticsEvent {

    data class MapClicked(
        val college: Long,
        val major: Long,
        val isFestival: Boolean,
    ) : MapAnalyticsEvent {
        override val eventName = "click_map"
        override val properties = buildMap {
            put("college", college)
            put("major", major)
            put("default_type", if (isFestival) "festival" else "general")
        }
    }

    data class AllClicked(
        val college: Long,
        val major: Long,
    ) : MapAnalyticsEvent {
        override val eventName = "click_map_all"
        override val properties = buildMap {
            put("college", college)
            put("major", major)
        }
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

    data class FestivalClicked(
        val college: Long,
        val major: Long,
    ) : MapAnalyticsEvent {
        override val eventName = "click_map_festival"
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
            putRestaurant("restaurants", restaurant)
        }
    }

    data class Removed(
        val restaurant: Restaurant? = null,
    ) : WidgetAnalyticsEvent {
        override val eventName = "remove_widget"
        override val properties = buildMap {
            restaurant?.let { putRestaurant("restaurants", it) }
        }
    }

    data class Changed(
        val restaurantBefore: Restaurant,
        val restaurantAfter: Restaurant,
    ) : WidgetAnalyticsEvent {
        override val eventName = "change_widget"
        override val properties = buildMap {
            putRestaurant("restaurant_before", restaurantBefore)
            putRestaurant("restaurant_after", restaurantAfter)
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

sealed interface CredentialsEvent : AnalyticsEvent {
    data class ClickLoginEvent(
        val loginMethod: String
    ) : CredentialsEvent {
        override val eventName = "click_login"
        override val properties = buildMap {
            put("method", loginMethod)
        }
    }

    data class CompleteLoginEvent(
        val loginMethod: String
    ) : CredentialsEvent {
        override val eventName = "complete_login"
        override val properties = buildMap {
            put("method", loginMethod)
        }
    }
}

data class ClickPlzNotMeEvent(
    val college: Long,
    val major: Long,
) : AppAnalyticsEvent {
    override val eventName = "click_plz_not_me"
    override val properties = buildMap {
        put("college", college)
        put("major", major)
    }
}

data class ClickMyPageMenuEvent(
    val college: Long,
    val major: Long,
    val menu: String,
) : AppAnalyticsEvent {
    override val eventName = "click_mypage_menu"
    override val properties = buildMap {
        put("college", college)
        put("major", major)
        put("menu", menu)
    }
}

data class PopupEvent(
    val popupName: String,
    val popupAction: String,
) : AppAnalyticsEvent {
    override val eventName = "popup_event"
    override val properties = buildMap {
        put("popup_name", popupName)
        put("popup_action", popupAction)
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

private fun MutableMap<String, Any>.putRestaurant(key: String, restaurant: Restaurant) {
    if (restaurant in analyticsExcludedRestaurants) return
    put(key, restaurant.value)
}

private val analyticsExcludedRestaurants = setOf(
    Restaurant.FOOD_COURT,
    Restaurant.THE_KITCHEN,
)
