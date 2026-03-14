package com.eatssu.android.analytics

import com.eatssu.common.analytics.AnalyticsDestination
import com.eatssu.common.analytics.AnalyticsEvent
import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.AnalyticsTracker
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber
import kotlin.jvm.JvmSuppressWildcards

@Singleton
class DefaultAnalyticsTracker @Inject constructor(
    destinations: Set<@JvmSuppressWildcards AnalyticsDestination>,
) : AnalyticsTracker {

    private val destinations: List<AnalyticsDestination> = destinations.distinctBy(AnalyticsDestination::id)

    override fun track(event: AnalyticsEvent) {
        destinations.forEach { destination ->
            runCatching {
                destination.track(event)
            }.onFailure { throwable ->
                Timber.e(
                    throwable,
                    "Failed to track analytics event %s via %s",
                    event.eventName,
                    destination.id,
                )
            }
        }
    }

    override fun identify(identity: AnalyticsIdentity) {
        if (identity.distinctId.isBlank()) return

        destinations.forEach { destination ->
            runCatching {
                destination.identify(identity)
            }.onFailure { throwable ->
                Timber.e(
                    throwable,
                    "Failed to identify analytics user via %s",
                    destination.id,
                )
            }
        }
    }

    override fun resetIdentity() {
        destinations.forEach { destination ->
            runCatching {
                destination.resetIdentity()
            }.onFailure { throwable ->
                Timber.e(
                    throwable,
                    "Failed to reset analytics identity via %s",
                    destination.id,
                )
            }
        }
    }
}
