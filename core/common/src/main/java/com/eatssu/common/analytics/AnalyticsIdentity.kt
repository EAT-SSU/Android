package com.eatssu.common.analytics

data class AnalyticsIdentity(
    val distinctId: String,
    val email: String,
    val nickname: String? = null,
    val collegeId: Int? = null,
    val collegeName: String? = null,
    val departmentId: Int? = null,
    val departmentName: String? = null,
)
