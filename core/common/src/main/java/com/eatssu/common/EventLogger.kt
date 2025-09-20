package com.eatssu.common

import android.os.Bundle
import com.eatssu.common.enums.EventType
import com.eatssu.common.enums.FirebaseScreenId
import com.eatssu.common.enums.LaunchPath
import com.eatssu.common.enums.Restaurant
import com.eatssu.common.enums.Time
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ParametersBuilder
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase

private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }

object EventLogger {

    fun setUserProperties(vararg properties: Pair<String, String>) {
        properties.forEach { property ->
            firebaseAnalytics.setUserProperty(property.first, property.second)
        }
    }

    fun appLaunch(launchPath: LaunchPath) {
        logEvent(EventType.APP_LAUNCH) {
            param("launch_path", launchPath.value)
        }
    }

    fun clickRestaurantInfo(restaurant: Restaurant) {
        logEvent(EventType.CLICK_RESTAURANT_INFO) {
            param("restaurants", restaurant.value)
        }
    }

    fun selectMealTime(time: Time) {
        logEvent(EventType.SELECT_MEALTIME) {
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
        logEvent(EventType.CLICK_DAY) {
            param("day", weekDay)
        }
    }

    fun clickMenu(restaurant: Restaurant) {
        logEvent(EventType.CLICK_MENU) {
            param("restaurants", restaurant.value)
        }
    }

    fun writeReview() { //todo v2로 바꿀시 v1 제거
        logEvent(EventType.WRITE_REVIEW_V1)
    }

    fun completeReviewV1(
        rating: Long,
        selection: Long,
        photoAttached: Boolean,
    ) {
        logEvent(EventType.COMPLETE_REVIEW_V1) {
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
        logEvent(EventType.COMPLETE_REVIEW_V2) {
            param("rating", rating)
            param("likes", likes)
            param("photo_attached", if (photoAttached) 1 else 0)
        }
    }


    fun clickMap() {
        logEvent(EventType.CLICK_MAP)
    }

    fun clickMapMine(
        college: Long,
        major: Long,
    ) {
        logEvent(EventType.CLICK_MAP_MINE) {
            param("college", college)
            param("major", major)
        }
    }

    fun clickPartnerRestaurant(
        college: Long,
        major: Long,
        partnerRestaurantId: Long
    ) {
        logEvent(EventType.CLICK_PARTNER_RESTAURANT) {
            param("college", college)
            param("major", major)
            param("partner_restaurant_id", partnerRestaurantId)
        }
    }

    fun addWidget(restaurant: Restaurant) {
        logEvent(EventType.ADD_WIDGET) {
            param("restaurants", restaurant.value)
        }
    }

    fun removeWidget() {
        logEvent(EventType.REMOVE_WIDGET)
    }

    //todo 파라미터 넣을지 추후 논의
    fun removeWidget(restaurant: Restaurant) {
        logEvent(EventType.REMOVE_WIDGET) {
            param("restaurants", restaurant.value)
        }
    }

    //todo change_widget
    fun changeWidget(restaurant: Restaurant) {
        logEvent(EventType.CHANGE_WIDGET) {
            param("restaurants", restaurant.value)
        }
    }

    fun screenView(screenId: FirebaseScreenId, screenClass: String) {
        logEvent(EventType.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenId.value)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
    }

    private fun logEvent(
        eventType: EventType,
        bundle: Bundle? = null
    ) {
        firebaseAnalytics.logEvent(eventType.value, bundle)
    }

    private fun logEvent(
        eventType: EventType,
        block: ParametersBuilder.() -> Unit
    ) {
        firebaseAnalytics.logEvent(eventType.value, block)
    }
}