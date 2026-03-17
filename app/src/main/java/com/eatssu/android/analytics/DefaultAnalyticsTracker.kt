package com.eatssu.android.analytics

import com.eatssu.common.analytics.AnalyticsEvent
import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.AnalyticsTracker
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber
import kotlin.jvm.JvmSuppressWildcards

@Singleton
class DefaultAnalyticsTracker @Inject constructor(
    trackers: Set<@JvmSuppressWildcards AnalyticsTracker>,
) : AnalyticsTracker {

    override val id: String = "default"

    private val trackers: List<AnalyticsTracker> = trackers.distinctBy(AnalyticsTracker::id)

    override fun track(event: AnalyticsEvent) {
        trackers.forEach { tracker ->
            runCatching {
                tracker.track(event)
            }.onFailure { throwable ->
                Timber.e(
                    throwable,
                    "Failed to track analytics event %s via %s",
                    event.eventName,
                    tracker.id,
                )
            }
        }
    }

    override fun identify(identity: AnalyticsIdentity) {
        if (identity.distinctId.isBlank()) return

        trackers.forEach { tracker ->
            runCatching {
                tracker.identify(identity)
            }.onFailure { throwable ->
                Timber.e(
                    throwable,
                    "Failed to identify analytics user via %s",
                    tracker.id,
                )
            }
        }
    }

    override fun resetIdentity() {
        trackers.forEach { tracker ->
            runCatching {
                tracker.resetIdentity()
            }.onFailure { throwable ->
                Timber.e(
                    throwable,
                    "Failed to reset analytics identity via %s",
                    tracker.id,
                )
            }
        }
    }
}
