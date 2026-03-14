package com.eatssu.android.analytics

import android.os.Bundle
import com.eatssu.common.analytics.AnalyticsDestination
import com.eatssu.common.analytics.AnalyticsEvent
import com.eatssu.common.analytics.AnalyticsIdentity
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseAnalyticsDestination @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsDestination {
    override val id: String = "firebase"

    override fun track(event: AnalyticsEvent) {
        val payload = event.toPayload()
        firebaseAnalytics.logEvent(payload.eventName, payload.properties.toBundle())
    }

    override fun identify(identity: AnalyticsIdentity) {
        firebaseAnalytics.setUserId(identity.distinctId)
        identity.toProperties().forEach { (key, value) ->
            firebaseAnalytics.setUserProperty(key, value.toString())
        }
    }

    override fun resetIdentity() {
        firebaseAnalytics.setUserId(null)
    }
}

internal fun Map<String, Any>.toBundle(): Bundle =
    Bundle().apply {
        forEach { (key, value) ->
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Double -> putDouble(key, value)
                is Float -> putFloat(key, value)
                is Boolean -> putBoolean(key, value)
                is Bundle -> putBundle(key, value)
                else -> putString(key, value.toString())
            }
        }
    }
