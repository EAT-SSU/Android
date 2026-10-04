package com.eatssu.android.analytics

import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.AppAnalyticsEvent
import com.eatssu.common.analytics.ChangeLanguageEvent
import com.eatssu.common.analytics.PopupEvent
import com.eatssu.common.analytics.WidgetAnalyticsEvent
import com.eatssu.common.enums.LaunchPath
import com.eatssu.common.enums.Restaurant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PostHogAnalyticsTrackerTest {

    @Test
    fun `launch payload keeps posthog compatible schema`() {
        val payload = AppAnalyticsEvent.Launch(LaunchPath.WIDGET, "ko").toPayload()

        assertEquals("app_launch", payload.eventName)
        assertEquals(
            mapOf(
                "launch_path" to "widget",
                "localeCode" to "ko",
            ),
            payload.properties,
        )
    }

    @Test
    fun `change language payload keeps previous and next locale codes`() {
        val payload = ChangeLanguageEvent(
            lang_from = "ko",
            lang_to = "en",
        ).toPayload()

        assertEquals("change_language", payload.eventName)
        assertEquals(
            mapOf(
                "lang_from" to "ko",
                "lang_to" to "en",
            ),
            payload.properties,
        )
    }

    @Test
    fun `popup event payload keeps popup name and action`() {
        val payload = PopupEvent(
            popupName = "plz_not_me",
            popupAction = "click_popup_image",
        ).toPayload()

        assertEquals("popup_event", payload.eventName)
        assertEquals(
            mapOf(
                "popup_name" to "plz_not_me",
                "popup_action" to "click_popup_image",
            ),
            payload.properties,
        )
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
    fun `restaurant analytics payload omits excluded restaurants`() {
        val addPayload = WidgetAnalyticsEvent.Added(Restaurant.THE_KITCHEN).toPayload()
        val removePayload = WidgetAnalyticsEvent.Removed(Restaurant.THE_KITCHEN).toPayload()

        assertTrue(addPayload.properties.isEmpty())
        assertTrue(removePayload.properties.isEmpty())
    }

    @Test
    fun `widget change payload keeps before and after restaurant keys`() {
        val payload = WidgetAnalyticsEvent.Changed(
            restaurantBefore = Restaurant.HAKSIK,
            restaurantAfter = Restaurant.DODAM,
        ).toPayload()

        assertEquals("change_widget", payload.eventName)
        assertEquals(
            mapOf(
                "restaurant_before" to "haksik",
                "restaurant_after" to "dodam",
            ),
            payload.properties,
        )
    }

    @Test
    fun `widget change payload omits excluded before and after restaurant values`() {
        val payload = WidgetAnalyticsEvent.Changed(
            restaurantBefore = Restaurant.THE_KITCHEN,
            restaurantAfter = Restaurant.THE_KITCHEN,
        ).toPayload()

        assertEquals("change_widget", payload.eventName)
        assertTrue(payload.properties.isEmpty())
    }
}
