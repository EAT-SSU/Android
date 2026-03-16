package com.eatssu.android.analytics

import com.eatssu.common.analytics.ReviewAnalyticsEvent
import com.eatssu.common.analytics.ScreenViewEvent
import com.eatssu.common.enums.ScreenId
import org.junit.Assert.assertEquals
import org.junit.Test

class FirebaseAnalyticsTrackerTest {

    @Test
    fun `screen view payload uses firebase screen keys`() {
        val payload = ScreenViewEvent(
            screenId = ScreenId.HOME_MAIN,
            screenClass = "MainActivity",
        ).toPayload()

        assertEquals("screen_view", payload.eventName)
        assertEquals(
            mapOf(
                "screen_name" to ScreenId.HOME_MAIN.value,
                "screen_class" to "MainActivity",
            ),
            payload.properties,
        )
    }

    @Test
    fun `review completion payload keeps firebase compatible numeric values`() {
        val payload = ReviewAnalyticsEvent.Completed(
            rating = 5L,
            likes = 2L,
            photoAttached = true,
        ).toPayload()

        assertEquals("complete_review_v2", payload.eventName)
        assertEquals(
            mapOf(
                "rating" to 5L,
                "likes" to 2L,
                "photo_attached" to 1,
            ),
            payload.properties,
        )
    }
}
