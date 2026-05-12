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

sealed interface LoginAnalyticsEvent : AnalyticsEvent {
    enum class Method(val value: String) {
        KAKAO("kakao"),
        APPLE("apple"),
        GUEST("guest"),
    }

    data class Clicked(
        val method: Method,
    ) : LoginAnalyticsEvent {
        override val eventName = "click_login"
        override val properties = buildMap {
            put("method", method.value)
        }
    }

    data class Completed(
        val method: Method,
    ) : LoginAnalyticsEvent {
        override val eventName = "complete_login"
        override val properties = buildMap {
            put("method", method.value)
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
            put("photo_attached", if (photoAttached) 1L else 0L)
        }
    }
}

sealed interface MapAnalyticsEvent : AnalyticsEvent {
    object EntryClicked : MapAnalyticsEvent {
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

sealed interface AnyoneButMeAnalyticsEvent : AnalyticsEvent {
    data class Clicked(
        val college: Long,
        val major: Long,
    ) : AnyoneButMeAnalyticsEvent {
        override val eventName = "click_plz_not_me"
        override val properties = buildMap {
            put("college", college)
            put("major", major)
        }
    }
}

sealed interface MyPageAnalyticsEvent : AnalyticsEvent {
    object MenuClicked : MyPageAnalyticsEvent {
        override val eventName = "click_mypage_menu"
        override val properties = emptyMap<String, Any>()
    }
}

sealed interface PopupAnalyticsEvent : AnalyticsEvent {
    enum class Action(val value: String) {
        CLICK_POPUP_IMAGE("click_popup_image"),
        GO_INSTA("go_insta"),
        NOT_SHOW_AGAIN("not_show_again"),
        CLOSE("close"),
    }

    data class AnyoneButMe(
        val action: Action,
    ) : PopupAnalyticsEvent {
        override val eventName = "popup_event"
        override val properties = buildMap {
            put("popup_name", "plz_not_me")
            put("popup_action", action.value)
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
        val restaurantBefore: Restaurant,
        val restaurantAfter: Restaurant,
    ) : WidgetAnalyticsEvent {
        override val eventName = "change_widget"
        override val properties = buildMap {
            put("restaurant_before", restaurantBefore.value)
            put("restaurant_after", restaurantAfter.value)
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
