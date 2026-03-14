package com.eatssu.android.analytics

import com.eatssu.common.analytics.AnalyticsDestination
import com.eatssu.common.analytics.AnalyticsEvent
import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.MapAnalyticsEvent
import com.eatssu.common.analytics.ReviewAnalyticsEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultAnalyticsTrackerTest {

    @Test
    fun `track dispatches typed event to all destinations`() {
        val firebaseDestination = FakeAnalyticsDestination(id = "firebase")
        val postHogDestination = FakeAnalyticsDestination(id = "posthog")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(firebaseDestination, postHogDestination))
        val event = ReviewAnalyticsEvent.Completed(rating = 5L, likes = 2L, photoAttached = true)

        analyticsTracker.track(event)

        assertEquals(listOf(event), firebaseDestination.events)
        assertEquals(listOf(event), postHogDestination.events)
    }

    @Test
    fun `duplicate destination ids are de duplicated`() {
        val first = FakeAnalyticsDestination(id = "duplicate")
        val second = FakeAnalyticsDestination(id = "duplicate")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(first, second))

        analyticsTracker.track(MapAnalyticsEvent.AllClicked)

        assertEquals(1, first.events.size + second.events.size)
    }

    @Test
    fun `identify propagates typed identity to all destinations`() {
        val firebaseDestination = FakeAnalyticsDestination(id = "firebase")
        val postHogDestination = FakeAnalyticsDestination(id = "posthog")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(firebaseDestination, postHogDestination))
        val identity = AnalyticsIdentity(
            distinctId = "test@soongsil.ac.kr",
            email = "test@soongsil.ac.kr",
            nickname = "eatssu",
        )

        analyticsTracker.identify(identity)

        assertEquals(listOf(identity), firebaseDestination.identities)
        assertEquals(listOf(identity), postHogDestination.identities)
    }

    @Test
    fun `track isolates destination failures`() {
        val failingDestination = FakeAnalyticsDestination(id = "firebase", failOnTrack = true)
        val healthyDestination = FakeAnalyticsDestination(id = "posthog")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(failingDestination, healthyDestination))
        val event = MapAnalyticsEvent.AllClicked

        analyticsTracker.track(event)

        assertTrue(failingDestination.trackAttempted)
        assertEquals(listOf(event), healthyDestination.events)
    }

    @Test
    fun `resetIdentity resets every sink`() {
        val firebaseDestination = FakeAnalyticsDestination(id = "firebase")
        val postHogDestination = FakeAnalyticsDestination(id = "posthog")
        val analyticsTracker = DefaultAnalyticsTracker(setOf(firebaseDestination, postHogDestination))

        analyticsTracker.resetIdentity()

        assertTrue(firebaseDestination.resetCalled)
        assertTrue(postHogDestination.resetCalled)
    }

    private class FakeAnalyticsDestination(
        override val id: String,
        private val failOnTrack: Boolean = false,
    ) : AnalyticsDestination {
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
