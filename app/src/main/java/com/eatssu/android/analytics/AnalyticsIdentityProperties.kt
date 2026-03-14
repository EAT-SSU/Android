package com.eatssu.android.analytics

import com.eatssu.common.analytics.AnalyticsIdentity

internal fun AnalyticsIdentity.toProperties(): Map<String, Any> =
    buildMap {
        put("email", email)
        nickname?.let { put("nickname", it) }
        collegeId?.let { put("college_id", it) }
        collegeName?.let { put("college_name", it) }
        departmentId?.let { put("department_id", it) }
        departmentName?.let { put("department_name", it) }
    }
