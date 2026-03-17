package com.eatssu.common.analytics

data class AnalyticsPayload(
    val eventName: String,
    val properties: Map<String, Any> = emptyMap(),
)
