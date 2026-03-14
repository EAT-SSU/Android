package com.eatssu.common.analytics

/**
 * AnalyticsDestination: 개별 analytics 백엔드 전송 어댑터.
 * Tracker가 받은 typed event를 각 SDK 형식으로 변환/전송한다.
 */
interface AnalyticsDestination {
    val id: String

    fun track(event: AnalyticsEvent)

    fun identify(identity: AnalyticsIdentity)

    fun resetIdentity()
}
