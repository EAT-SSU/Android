package com.eatssu.android.analytics

import com.eatssu.android.domain.model.College
import com.eatssu.android.domain.model.Department
import com.eatssu.common.analytics.AnalyticsEvent
import com.eatssu.common.analytics.AnalyticsIdentity
import com.eatssu.common.analytics.AnalyticsTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsIdentityManagerTest {

    private val tracker = FakeAnalyticsTracker()
    private val manager = AnalyticsIdentityManager(tracker)

    @Test
    fun `blank email is ignored`() {
        manager.identifyUser(email = "   ")

        assertTrue(tracker.identities.isEmpty())
    }

    @Test
    fun `identifyUser trims values and removes placeholder college department`() {
        manager.identifyUser(
            email = " test@soongsil.ac.kr ",
            nickname = " eatssu ",
            college = College(collegeId = -1, collegeName = "단과대"),
            department = Department(departmentId = -1, departmentName = "학과"),
        )

        val identity = tracker.identities.single()
        assertEquals("test@soongsil.ac.kr", identity.distinctId)
        assertEquals("test@soongsil.ac.kr", identity.email)
        assertEquals("eatssu", identity.nickname)
        assertNull(identity.collegeId)
        assertNull(identity.collegeName)
        assertNull(identity.departmentId)
        assertNull(identity.departmentName)
    }

    @Test
    fun `identifyUser keeps valid college and department`() {
        manager.identifyUser(
            email = "test@soongsil.ac.kr",
            college = College(collegeId = 1, collegeName = "IT대"),
            department = Department(departmentId = 2, departmentName = "소프트웨어학부"),
        )

        assertEquals(
            AnalyticsIdentity(
                distinctId = "test@soongsil.ac.kr",
                email = "test@soongsil.ac.kr",
                collegeId = 1,
                collegeName = "IT대",
                departmentId = 2,
                departmentName = "소프트웨어학부",
            ),
            tracker.identities.single(),
        )
    }

    @Test
    fun `resetIdentity delegates to tracker`() {
        manager.resetIdentity()

        assertTrue(tracker.resetCalled)
    }

    private class FakeAnalyticsTracker : AnalyticsTracker {
        override val id: String = "fake"

        val identities = mutableListOf<AnalyticsIdentity>()
        var resetCalled: Boolean = false

        override fun track(event: AnalyticsEvent) = Unit

        override fun identify(identity: AnalyticsIdentity) {
            identities += identity
        }

        override fun resetIdentity() {
            resetCalled = true
        }
    }
}
