package com.eatssu.android.analytics

import com.eatssu.common.analytics.AnalyticsEvent
import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.AnalyticsTracker
import com.posthog.PostHogInterface
import javax.inject.Inject

class PostHogAnalyticsTracker @Inject constructor(
    private val postHog: PostHogInterface,
) : AnalyticsTracker {
    override val id: String = "posthog"

    override fun track(event: AnalyticsEvent) {
        val payload = event.toPayload()
        postHog.capture(
            event = payload.eventName,
            properties = payload.properties.takeIf { it.isNotEmpty() },
        )
    }

    override fun identify(identity: AnalyticsIdentity) {
        postHog.identify(
            distinctId = identity.distinctId,
            userProperties = identity.toProperties().takeIf { it.isNotEmpty() },
        )
    }

    override fun resetIdentity() {
        postHog.reset()
    }
}
