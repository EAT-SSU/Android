package com.eatssu.common

import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase

private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }

enum class LaunchPath(val value: String) {
    ICON("icon"),
    LOCAL_NOTIFICATION("local_notification"),
    WIDGET("widget"),
}

enum class Day(val value: String) {
    MONDAY("mon"),
    TUESDAY("tue"),
    WEDNESDAY("wed"),
    THURSDAY("thu"),
    FRIDAY("fri"),
    SATURDAY("sat"),
    SUNDAY("sun")
}

object EventLogger {

    fun setUserProperties(vararg properties: Pair<String, String>) {
        properties.forEach { property ->
            firebaseAnalytics.setUserProperty(property.first, property.second)
        }
    }

    fun appLaunch(launchPath: LaunchPath) {
        firebaseAnalytics.logEvent("app_launch") {
            param("launch_path", launchPath.value)
        }
    }

    fun clickCafeteriaInfo(restaurant: Restaurant) {
        firebaseAnalytics.logEvent("click_cafeteria_info") {
            param("restaurants", restaurant.value)
        }
    }

    fun selectMealtype(time: Time) {
        firebaseAnalytics.logEvent("select_mealtype") {
            param("restaurants", time.value)
        }
    }

    fun clickDay(day: String) {
        val weekDay = when (day) {
            "SUNDAY" -> "sun"
            "MONDAY" -> "mon"
            "TUESDAY" -> "tue"
            "WEDNESDAY" -> "wed"
            "THURSDAY" -> "thu"
            "FRIDAY" -> "fri"
            "SATURDAY" -> "sat"
            else -> {
                ""
            }
        }
        firebaseAnalytics.logEvent("click_day") {
            param("restaurants", weekDay)
        }
    }

    fun clickMenu(restaurant: Restaurant) {
        firebaseAnalytics.logEvent("click_menu") {
            param("restaurants", restaurant.value)
        }
    }

    fun writeReview() { //todo v2로 바꿀시 v1 제거
        firebaseAnalytics.logEvent("write_review_v1", null)
    }

    fun completeReview(
        rating: Long,
        likes: List<Long>,
        photoAttached: Boolean,
    ) {
        firebaseAnalytics.logEvent("complete_review_v1") {
            param("rating", rating)
            param("likes", likes.joinToString(","))
            param("photoAttached", if (photoAttached) 1 else 0)
        }
    }

    fun clickMap() {
        firebaseAnalytics.logEvent("click_map", null)
    }

    fun clickMapMine(
        college: Long,
        major: Long,
    ) {
        firebaseAnalytics.logEvent("click_map_mine") {
            param("college", college)
            param("major", major)
        }
    }

    fun clickPartnerRestaurant(
        college: Long,
        major: Long,
        partnerRestaurantId: Long
    ) {
        firebaseAnalytics.logEvent("click_partner_restaurant") {
            param("college", college)
            param("major", major)
            param("partner_restaurant_ID", partnerRestaurantId)
        }
    }

    fun addWidget(restaurant: Restaurant) {
        firebaseAnalytics.logEvent("add_widget") {
            param("restaurant", restaurant.value)
        }
    }

    fun removeWidget(restaurant: Restaurant) {
        firebaseAnalytics.logEvent("remove_widget") {
            param("restaurant", restaurant.value)
        }
    }

    //todo change_widget
}