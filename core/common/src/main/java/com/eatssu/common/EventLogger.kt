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

    fun clickRestaurantInfo(restaurant: Restaurant) {
        firebaseAnalytics.logEvent("click_restaurant_info") {
            param("restaurants", restaurant.value)
        }
    }

    fun selectMealTime(time: Time) {
        firebaseAnalytics.logEvent("select_mealtime") {
            param("mealtime", time.value)
        }
    }

    fun selectDay(day: String) {
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
            param("day", weekDay)
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

    fun completeReviewV1(
        rating: Long,
        selection: Long,
        photoAttached: Boolean,
    ) {
        firebaseAnalytics.logEvent("complete_review_v1") {
            param("rating", rating)
            param("selection", selection)
            param("photo_attached", if (photoAttached) 1 else 0)
        }
    }

    fun completeReviewV2(
        rating: Long,
        likes: Long,
        photoAttached: Boolean,
    ) {
        firebaseAnalytics.logEvent("complete_review_v1") {
            param("rating", rating)
            param("likes", likes)
            param("photo_attached", if (photoAttached) 1 else 0)
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
            param("partner_restaurant_id", partnerRestaurantId)
        }
    }

    fun addWidget(restaurant: Restaurant) {
        firebaseAnalytics.logEvent("add_widget") {
            param("restaurants", restaurant.value)
        }
    }

    fun removeWidget() {
        firebaseAnalytics.logEvent("remove_widget", null)
    }

    //todo 파라미터 넣을지 추후 논의
    fun removeWidget(restaurant: Restaurant) {
        firebaseAnalytics.logEvent("remove_widget") {
            param("restaurants", restaurant.value)
        }
    }

    //todo change_widget
    fun changeWidget(restaurant: Restaurant) {
        firebaseAnalytics.logEvent("change_widget") {
            param("restaurants", restaurant.value)
        }
    }
}