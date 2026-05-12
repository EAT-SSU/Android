package com.eatssu.android.analytics

import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.AppAnalyticsEvent
import com.eatssu.common.analytics.LoginAnalyticsEvent
import com.eatssu.common.analytics.WidgetAnalyticsEvent
import com.eatssu.common.enums.LaunchPath
import com.eatssu.common.enums.Restaurant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PostHogAnalyticsTrackerTest {

    @Test
    fun `launch payload keeps posthog compatible schema`() {
        val payload = AppAnalyticsEvent.Launch(LaunchPath.WIDGET).toPayload()

        assertEquals("app_launch", payload.eventName)
        assertEquals(mapOf("launch_path" to "widget"), payload.properties)
    }

    @Test
    fun `remote notification launch payload keeps distinct path`() {
        val payload = AppAnalyticsEvent.Launch(LaunchPath.REMOTE_NOTIFICATION).toPayload()

        assertEquals("app_launch", payload.eventName)
        assertEquals(mapOf("launch_path" to "remote_notification"), payload.properties)
    }

    @Test
    fun `widget removal without restaurant omits properties`() {
        val payload = WidgetAnalyticsEvent.Removed().toPayload()

        assertEquals("remove_widget", payload.eventName)
        assertTrue(payload.properties.isEmpty())
    }

    @Test
    fun `identity properties exclude null values`() {
        val properties = AnalyticsIdentity(
            distinctId = "test@soongsil.ac.kr",
            email = "test@soongsil.ac.kr",
            nickname = "eatssu",
            collegeId = 1,
            collegeName = "IT대",
            departmentId = null,
            departmentName = null,
        ).toProperties()

        assertEquals(
            mapOf(
                "email" to "test@soongsil.ac.kr",
                "nickname" to "eatssu",
                "college_id" to 1,
                "college_name" to "IT대",
            ),
            properties,
        )
    }

    @Test
    fun `widget addition payload keeps restaurant key`() {
        val payload = WidgetAnalyticsEvent.Added(Restaurant.HAKSIK).toPayload()

        assertEquals("add_widget", payload.eventName)
        assertEquals(mapOf("restaurants" to "haksik"), payload.properties)
    }

    @Test
    fun `widget change payload keeps before and after keys`() {
        val payload = WidgetAnalyticsEvent.Changed(
            restaurantBefore = Restaurant.DODAM,
            restaurantAfter = Restaurant.HAKSIK,
        ).toPayload()

        assertEquals("change_widget", payload.eventName)
        assertEquals(
            mapOf(
                "restaurant_before" to "dodam",
                "restaurant_after" to "haksik",
            ),
            payload.properties,
        )
    }

    @Test
    fun `login payload keeps method schema`() {
        val payload = LoginAnalyticsEvent.Clicked(LoginAnalyticsEvent.Method.KAKAO).toPayload()

        assertEquals("click_login", payload.eventName)
        assertEquals(mapOf("method" to "kakao"), payload.properties)
    }
}
