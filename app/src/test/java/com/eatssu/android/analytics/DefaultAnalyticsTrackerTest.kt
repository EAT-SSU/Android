package com.eatssu.android.analytics

import com.eatssu.common.analytics.AnalyticsEvent
import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.MapAnalyticsEvent
import com.eatssu.common.analytics.AnalyticsTracker
import com.eatssu.common.analytics.ReviewAnalyticsEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAnalyticsTrackerTest {

    @Test
    fun `track dispatches typed event to all trackers`() {
        val firebaseTracker = FakeAnalyticsTracker(id = "firebase")
        val postHogTracker = FakeAnalyticsTracker(id = "posthog")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(firebaseTracker, postHogTracker))
        val event = ReviewAnalyticsEvent.Completed(rating = 5L, likes = 2L, photoAttached = true)

        analyticsTracker.track(event)

        assertEquals(listOf(event), firebaseTracker.events)
        assertEquals(listOf(event), postHogTracker.events)
    }

    @Test
    fun `duplicate tracker ids are de duplicated`() {
        val first = FakeAnalyticsTracker(id = "duplicate")
        val second = FakeAnalyticsTracker(id = "duplicate")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(first, second))

        analyticsTracker.track(MapAnalyticsEvent.AllClicked)

        assertEquals(1, first.events.size + second.events.size)
    }

    @Test
    fun `identify propagates typed identity to all trackers`() {
        val firebaseTracker = FakeAnalyticsTracker(id = "firebase")
        val postHogTracker = FakeAnalyticsTracker(id = "posthog")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(firebaseTracker, postHogTracker))
        val identity = AnalyticsIdentity(
            distinctId = "test@soongsil.ac.kr",
            email = "test@soongsil.ac.kr",
            nickname = "eatssu",
        )

        analyticsTracker.identify(identity)

        assertEquals(listOf(identity), firebaseTracker.identities)
        assertEquals(listOf(identity), postHogTracker.identities)
    }

    @Test
    fun `track isolates tracker failures`() {
        val failingTracker = FakeAnalyticsTracker(id = "firebase", failOnTrack = true)
        val healthyTracker = FakeAnalyticsTracker(id = "posthog")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(failingTracker, healthyTracker))
        val event = MapAnalyticsEvent.AllClicked

        analyticsTracker.track(event)

        assertTrue(failingTracker.trackAttempted)
        assertEquals(listOf(event), healthyTracker.events)
    }

    @Test
    fun `resetIdentity resets every tracker`() {
        val firebaseTracker = FakeAnalyticsTracker(id = "firebase")
        val postHogTracker = FakeAnalyticsTracker(id = "posthog")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(firebaseTracker, postHogTracker))

        analyticsTracker.resetIdentity()

        assertTrue(firebaseTracker.resetCalled)
        assertTrue(postHogTracker.resetCalled)
    }

    private class FakeAnalyticsTracker(
        override val id: String,
        private val failOnTrack: Boolean = false,
    ) : AnalyticsTracker {
        val events = mutableListOf<AnalyticsEvent>()
        val identities = mutableListOf<AnalyticsIdentity>()
        var resetCalled: Boolean = false
        var trackAttempted: Boolean = false

        override fun track(event: AnalyticsEvent) {
            trackAttempted = true
            if (failOnTrack) error("track failed")
            events += event
        }

        override fun identify(identity: AnalyticsIdentity) {
            identities += identity
        }

        override fun resetIdentity() {
            resetCalled = true
        }
    }
}
